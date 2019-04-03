package generic;

import java.util.HashMap;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * author Xianfeng <br/>
 * date 19-3-29 下午6:13 <br/>
 * Desc:
 */
public class HashMapTest {

    @Test
    public void saveNull() {
        HashMap<String, String> map = new HashMap<>();
        map.put(null, null);
        Assert.isTrue(map.size() == 1, "null 算作一个元素");
        Assert.isNull(map.get(null), "get value of null");
    }

    @Test
    public void saveGet() {
        HashMap<String, String> map = new HashMap<>();
        System.out.println(hash("1"));
        System.out.println((1 << 18) >> 16);

    }

    final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
