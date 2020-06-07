package inherit;

import org.junit.Test;

/**
 * 当一个类被继承的时，构造函数的行为
 */
public class SuperConstructTest {
    /**
     * 测试 父类构造函数中调用了会被继承的方法
     * 这里value两次会打印出不同的值，value=0是因为调用父类构造函数时
     * value还没初始化
     */
    @Test
    public void newSub() {
        Super sub = new Sub();
        sub.overrideMe();
    }

    class Super {

        public Super() {
            overrideMe();
        }

        public void overrideMe() {
        }

    }

    class Sub extends Super {
        int value;

        public Sub() {
            value = 42;
        }

        @Override
        public void overrideMe() {
            System.out.println("value:" + value);
        }
    }


}
