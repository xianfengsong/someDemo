package guava.cache;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import java.util.HashSet;
import java.util.Set;
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


    private static Cache<String, Bean> beanCache;
    private static Cache<String, String> stringCache;

    @Before
    public void init() {
        beanCache = CacheBuilder.newBuilder()
                //最多有多少键值对
                .maximumSize(20)
                //不能和maximumSize结合使用,单位是什么？
//                .maximumWeight()
                .concurrencyLevel(4)
                .expireAfterWrite(50, TimeUnit.SECONDS)
                //打开监控选项
                .recordStats()
                .build();
        stringCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .build();
    }

    /**
     * 测试guava cache 输出的监控信息
     */
    @Test
    public void testMonitor() throws InterruptedException {
        String key = "monitor";
        for (int i = 0; i < 10; i++) {
            Bean bean = new Bean(i + "xx", new HashSet<>());
            beanCache.put(key + i, bean);
        }
        for (int i = 0; i < 100; i++) {
            //guava cache 统计是实时的,不需要sleep()
//            Thread.sleep(100L);
            Bean bean = beanCache.getIfPresent(key + (i % 15));
            CacheStats stats = beanCache.stats();
            if (i % 10 == 0) {
                System.out.println("SUMMARY:" + stats);
                System.out.println(
                        String.format("命中率：%.2f%% value平均加载时间：%.2f miss率：%.2f%% 总请求次数：%d",
                                stats.hitRate() * 100, stats.averageLoadPenalty(),
                                stats.missRate() * 100, stats.requestCount()));
            }
        }

    }

    @Test
    public void testTimeout() {
        Bean bean = new Bean("xx", null);
        beanCache.put("bean", bean);
        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bean cache = beanCache.getIfPresent("bean");
        System.out.println(cache);
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

    /**
     * guava cache保存的对象，会随着原始对象改变
     */
    @Test
    public void cacheChangeWithObject() {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        Bean bean = new Bean("origin", set);
        beanCache.put("bean", bean);

        System.out.println("origin bean:" + bean + "\ncache bean:" + beanCache.getIfPresent("bean"));

        bean.set.add("c");
        bean.id = "origin_v1";
        String originChanged = bean.toString();
        String cache = beanCache.getIfPresent("bean").toString();
        Assert.assertEquals("cache没有随对象改变", originChanged, cache);

        System.out.println("changed bean:" + originChanged + "\ncache bean:" + beanCache.getIfPresent("bean"));
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
