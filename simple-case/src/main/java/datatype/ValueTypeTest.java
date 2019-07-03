package datatype;

import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-11 下午1:05 <br/>
 * Desc: Integer,String 均为传值，或者说不允许通过引用更新内存
 */
public class ValueTypeTest {

    /**
     * 函数内可以改变参数的字段
     */
    @Test
    public void testParamFieldCanChange() {
        Param origin = new Param(null, 0);

        funcA(origin);
        funcB(origin);
        Assert.assertEquals("fail", origin.getName(), "b");
        Assert.assertEquals("fail", origin.getValue(), 2);
        System.out.println(origin);
    }

    /**
     * 函数内不能改变参数的指向
     */
    @Test
    public void testParamPointerCantChange() {
        Param origin = null;
        funcA(origin);
        funcB(origin);
        Assert.assertNull(origin);
        System.out.println(origin);
    }

    private void funcA(Param param) {
        if (param == null) {
            param = new Param("a", 1);
        }
        System.out.println(param);
        param.setName("a");
        param.setValue(1);
    }

    private void funcB(Param param) {
        if (param == null) {
            param = new Param("b", 2);
        }
        System.out.println(param);
        param.setName("b");
        param.setValue(2);
    }

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

    class Param {

        String name;
        int value;

        public Param(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}
