package jvm.bytecode;

import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-18 下午5:36 <br/>
 * Desc:
 */
public class ByteCodeTest {

    //---ExceptionTableTest测试---

    /**
     * 测试inc()没有异常时，结果是什么
     * 返回值是1，但是x最后值为3
     */
    @Test
    public void testNoException() {
        ExceptionTableTest expTest = new ExceptionTableTest();
        Assert.assertEquals(1, expTest.inc());
        Assert.assertEquals(3, expTest.x);
    }

    /**
     * 测试inc()异常时，结果是什么
     * 返回值是2，但是x最后值为3
     */
    @Test
    public void testException() {
        ExceptionTableTest expTest = new ExceptionTableTest();

        expTest.causeException = true;
        Assert.assertEquals(2, expTest.inc());
        Assert.assertEquals(3, expTest.x);
    }

    /**
     * 测试switch-case
     */
    @Test
    public void test() {
        SwitchCaseTest.test(1);
    }

    /**
     * 测试synchronized的字节码
     */
    @Test
    public void syncTest() throws InterruptedException {
        Thread thread = new Thread(SynchronizedTest::test);
        thread.start();
        thread.join();
        SynchronizedTest.test();
        Assert.assertEquals(2, SynchronizedTest.getValue());

    }

}
