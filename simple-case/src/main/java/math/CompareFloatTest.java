package math;

import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-20 下午3:32 <br/>
 * Desc: 测试浮点数的比较，java不能精确表示0.1f，只能精确表示0.5f,0.25f ....
 */
public class CompareFloatTest {

    /**
     * 0.1f不能精确表示,0.5f可以
     */
    @Test
    public void compare() {
        float a = 1.0f - 0.9f;
        float b = 0.9f - 0.8f;
        Assert.assertFalse("不相等", a == b);
        Float x = Float.valueOf(a);
        Float y = Float.valueOf(b);
        Assert.assertFalse("还是不相等", x.equals(y));

        float c = 1.0f - 0.5f;
        float d = 1.5f - 1.0f;
        Assert.assertTrue("相等", c == d);
        Float s = Float.valueOf(a);
        Float t = Float.valueOf(b);
        Assert.assertFalse("还是相等", s.equals(t));

    }
}
