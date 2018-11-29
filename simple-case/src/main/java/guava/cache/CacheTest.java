package guava.cache;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xianfeng
 * E-Mail: songxianfeng@xiaomi.com
 * Date: 18-8-8 下午5:32
 * Desc:
 */
public class CacheTest {
    class Bean {
        String id;
        Set<String> set = new HashSet<>();

        @Override
        public String toString() {
            return "Bean{" +
                    "id='" + id + '\'' +
                    ", set=" + Joiner.on(",").join(set) +
                    '}';
        }
    }
    private static Cache<String, Bean> beanCache;
    public static void main(String []args){
        CacheTest t=new CacheTest();
        beanCache = CacheBuilder.newBuilder()
                .maximumSize(20)
                .concurrencyLevel(4)
                .expireAfterWrite(8, TimeUnit.HOURS)
                .build();
        Bean bean=t.new Bean();
        bean.id="old";
        bean.set.add("a");
        bean.set.add("b");

        beanCache.put("bean",bean);
        System.out.println(beanCache.getIfPresent("bean"));
        //注意cache中的对象也会被改变！
        bean.id="new";
        bean.set.add("c");
        System.out.println(beanCache.getIfPresent("bean"));
        boolean same = bean.hashCode()==beanCache.getIfPresent("bean").hashCode();

        System.out.println("is same?"+same);
        Bean c=t.new Bean();
        System.out.println("is same?"+(c.hashCode()==bean.hashCode()));
    }
}
