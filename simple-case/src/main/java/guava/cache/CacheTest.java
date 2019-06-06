package guava.cache;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Xianfeng
 * E-Mail:
 * Date: 18-8-8 下午5:32
 * Desc:
 */
public class CacheTest {


    private static Cache<String, Bean> beanCache, weightBeanCache;
    private static Cache<String, String> stringCache;
    //取不到值会去加载内容
    private static LoadingCache<String, Bean> loadingCache;

    @Before
    public void init() {
        beanCache = CacheBuilder.newBuilder()
                //最多有多少键值对
                .maximumSize(20)
                .concurrencyLevel(4)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                //打开监控选项
                .recordStats()
                .build();
        stringCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .build();
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(new CacheLoader<String, Bean>() {
                    @Override
                    public Bean load(String s) {
                        System.out.println("create bean! " + s);
                        return new Bean("new bean", new HashSet<>());
                    }
                });

        //自定义weight计算方式=set大小×10
        weightBeanCache = CacheBuilder.newBuilder()
                //不能和maximumSize结合使用
                .maximumWeight(100)
                .weigher((Weigher<String, Bean>) (key, value) -> value.getSet().size() * 10)
                .recordStats()
                .build();
    }

    @Test
    public void testGet() {
        String value = stringCache.getIfPresent("what");
        Assert.assertNull(value);
    }


    /**
     * 测试weak key/value cache & soft value cache
     * 1.主动gc后 weak value cache被回收
     * 2.使用了weak-key/value 和soft value的cache，在比较键时只能使用==，不支持equals
     */
    @Test
    public void testWeakSoft() {
        Bean weakKey = new Bean("i'm weak", null);
        Cache<Bean, Bean> weakKeyCache = CacheBuilder.newBuilder()
                .weakKeys()
                .build();
        Cache<String, Bean> weakValueCache = CacheBuilder.newBuilder()
                .weakValues()
                .build();
        Cache<String, Bean> softValueCache = CacheBuilder.newBuilder()
                .softValues()
                .build();
        weakKeyCache.put(weakKey, new Bean("wkk", new HashSet<>()));
        weakValueCache.put("weak-value", new Bean("wkv", new HashSet<>()));
        softValueCache.put("soft-value", new Bean("stv", new HashSet<>()));
        System.out.println(String.format("weak-key:%s \nweak-value:%s \nsoft-value:%s",
                weakKeyCache.getIfPresent(weakKey),
                weakValueCache.getIfPresent("weak-value"),
                softValueCache.getIfPresent("soft-value")));

        System.gc();
        Assert.assertNull("weak-value cache gc后消失", weakKeyCache.getIfPresent("weak-value"));

        System.out.println(String.format("After GC\nweak-key:%s \nweak-value:%s \nsoft-value:%s",
                weakKeyCache.getIfPresent(weakKey),
                weakValueCache.getIfPresent("weak-value"),
                softValueCache.getIfPresent("soft-value")));

        Bean weakKey2 = new Bean("i'm weak", null);
        Assert.assertNull("使用弱引用键的缓存用==而不是equals比较键", weakKeyCache.getIfPresent(weakKey2));

    }

    /**
     * 测试达到size上限时 cache如何回收
     */
    @Test
    public void testSize() {
        for (int i = 0; i < 10; i++) {
            stringCache.put("k" + i, "val");
        }
        System.out.println("get k0:" + stringCache.getIfPresent("k0"));
        stringCache.put("k10", "val");
        stringCache.put("k11", "val");
        //因为k0使用过，所有没有被回收
        //k1,k2被回收
        Assert.assertNotNull(stringCache.getIfPresent("k0"));
        System.out.println("get k0:" + stringCache.getIfPresent("k0"));
        System.out.println("get k1:" + stringCache.getIfPresent("k1"));
        System.out.println("get k2:" + stringCache.getIfPresent("k2"));

    }

    /**
     * 测试超过weight上限触发回收
     * 和size上限不同，设置weight上限，可以自定义每个k/v的weight，适合cache中元素大小差别大的情况
     * weight只保证cache不会超过这个上限，cache的回收不能直接根据weight来预测
     * 可能没到weight上限，cache就会被回收：Note that the cache may evict an entry before this limit is exceeded.
     */
    @Test
    public void testWeight() {
        Bean bean10 = new Bean("size10", getSets(1));
        Bean bean20 = new Bean("size20", getSets(2));
        Bean bean40 = new Bean("size40", getSets(4));
        Bean bean80 = new Bean("size40", getSets(8));
        Iterable<String> iterable = () -> Arrays.asList("10", "20", "40", "80").iterator();

        weightBeanCache.put("10", bean10);
        System.out.println("put 10:" + weightBeanCache.getAllPresent(iterable));

        weightBeanCache.put("20", bean20);
        System.out.println("put 20:" + weightBeanCache.getAllPresent(iterable));

        weightBeanCache.put("40", bean40);
        System.out.println("put 40:" + weightBeanCache.getAllPresent(iterable));
        Assert.assertNull("40根本没存入缓存", weightBeanCache.getIfPresent("40"));

        weightBeanCache.put("80", bean80);
        System.out.println("put 80:" + weightBeanCache.getAllPresent(iterable));
        Assert.assertNull("80根本没存入缓存", weightBeanCache.getIfPresent("80"));

        Map<String, Bean> all = weightBeanCache.getAllPresent(iterable);
        System.out.println("最后只有20留下，莫名其妙：" + all);
        System.out.println("被回收的数量：" + weightBeanCache.stats().evictionCount());

    }

    private Set<String> getSets(int size) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(String.valueOf((char) ('a' + i)));
        }
        return set;
    }

    /**
     * 测试loading cache ， 自动创建cache
     */
    @Test
    public void testLoadingCache() {
        try {
            //默认需要处理load时可能的异常
            Bean loaded = loadingCache.get("whatever");
            Assert.assertNotNull(loaded);
            loadingCache.getIfPresent(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //不抛出check exception的调用方式
        Bean loaded = loadingCache.getUnchecked("whatever");
        Assert.assertNotNull(loaded);

        //load一组 键值对
        Iterable<String> keys = () -> Arrays.asList("1", "2", "3").iterator();
        try {
            Map<String, Bean> beans = loadingCache.getAll(keys);
            Assert.assertEquals(3, beans.size());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试guava cache 输出的监控信息
     */
    @Test
    public void testMonitor() {
        String key = "monitor";
        for (int i = 0; i < 40; i++) {
            Bean bean = new Bean(i + "xx", new HashSet<>());
            beanCache.put(key + i, bean);
        }
        for (int i = 0; i < 100; i++) {
            //guava cache 统计是实时的,不需要sleep()
//            Thread.sleep(100L);
            beanCache.getIfPresent(key + (i % 15));
            CacheStats stats = beanCache.stats();
            if (i % 10 == 0) {
                System.out.println("SUMMARY:" + stats);
                System.out.println(
                        String.format("size:%d 命中率：%.2f%% value平均加载时间：%.2f miss率：%.2f%% "
                                        + "总请求次数：%d totalLoadTime: %d ns ,被驱逐节点数 %d ",
                                beanCache.size(),
                                stats.hitRate() * 100, stats.averageLoadPenalty(),
                                stats.missRate() * 100, stats.requestCount(),
                                stats.totalLoadTime(),
                                stats.evictionCount())
                );


            }
        }

    }

    /**
     * 测试超时时间,guava中的超时是通过处理查询请求被动完成的
     * 预期结果：超时后，在收到请求前cache size==1,并没有清理cache
     * 请求后size==0,cache才被清理
     */
    @Test
    public void testTimeout() {
        Bean bean = new Bean("xx", null);
        beanCache.put("bean", bean);
        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(beanCache.size());
        Assert.assertEquals("查询前size还是1", 1, beanCache.size());

        Bean cache = beanCache.getIfPresent("bean");
        System.out.println(beanCache.size());

        Assert.assertEquals("size还是1", 1, beanCache.size());
        Assert.assertNull("cache timeout", cache);
    }

    /**
     * 测试string类型value从cache取出后，修改不会影响到cache
     */
    @Test
    public void testChangeStringCache() {
        String value = "Hi";
        String key = "Key";
        stringCache.put(key, value);
        String cache = stringCache.getIfPresent(key);
        //修改cache
        cache = "Hello";
        Assert.assertEquals("string应该不会被修改，因为immutable", value, stringCache.getIfPresent(key));
        stringCache.getIfPresent(key);
    }

    /**
     * 测试从guava cache取出的对象被修改后，cache中的数据也被修改
     * 如果guava cache中取出的对象要修改，一定要先做深拷贝
     */
    @Test
    public void testChangeObjectCache() throws CloneNotSupportedException {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");

        Bean bean = new Bean("origin", set);
        beanCache.put("bean", bean);
        //修改cache对象
        Bean cache = beanCache.getIfPresent("bean");
        String newID = "newID";
        cache.setId(newID);
        //影响了缓存中的数据
        Assert.assertEquals(beanCache.getIfPresent("bean").getId(), newID);
        //拷贝cache对象，再修改
        cache = beanCache.getIfPresent("bean");
        Bean copy = (Bean) cache.clone();
        copy.setId("origin");
        //缓存数据不变
        Assert.assertEquals(beanCache.getIfPresent("bean").getId(), newID);
    }

    class Bean implements Cloneable {

        public Bean(String id, Set<String> set) {
            this.id = id;
            this.set = set;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Bean copy = (Bean) super.clone();
            copy.setId(this.getId());
            copy.setSet(this.getSet());

            return copy;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<String> getSet() {
            return set;
        }

        String id;
        Set<String> set = new HashSet<>();

        public void setSet(Set<String> set) {
            this.set = set;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "id='" + id + '\'' +
                    ", set=" + Joiner.on(",").join(set) +
                    '}';
        }
    }
}
