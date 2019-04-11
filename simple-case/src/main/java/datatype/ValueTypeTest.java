package datatype;

import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-11 下午1:05 <br/>
 * Desc: Integer,String 均为传值，或者说不允许通过引用更新内存
 */
public class ValueTypeTest {

    @Test
    public void swapIntegerTest() {
        Integer a = 1, b = 2;
        System.out.println("a=" + a + ",b=" + b);
        //pass value
        swap(a, b);
        Assert.assertEquals(1, (int) a);
        Assert.assertEquals(2, (int) b);

        System.out.println("a=" + a + ",b=" + b);
        Integer c = a;
        a = b;
        b = c;
        System.out.println("a=" + a + ",b=" + b);
        a = a ^ b;
        b = b ^ a;//b^(a^b)
        a = a ^ b;//a^(b^((a^b)^b))
        System.out.println("a=" + a + ",b=" + b);
    }

    private void swap(Integer a, Integer b) {
        a.intValue();
        b = 33;
    }

    private void swap(String a, String b) {

    }

    @Test
    public void swapStringTest() {
        String a = "1", b = "2";
        System.out.println("a=" + a + ",b=" + b);
        swap(a, b);
        System.out.println("a=" + a + ",b=" + b);

    }
}
