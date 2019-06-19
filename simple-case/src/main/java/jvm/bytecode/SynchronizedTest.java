package jvm.bytecode;

/**
 * author Xianfeng <br/>
 * date 19-6-18 下午8:03 <br/>
 * Desc: 测试synchronized的字节码
 * 有两种情况
 * (1) test方法中：synchronized块 编译出  monitorenter/exit 字节码指令
 * (2) getValue方法：对于 synchronized 方法 不需要 monitorenter/exit
 * 仔细看字节码中还有异常表
 */
public class SynchronizedTest {

    private static final Object LOCK = new Object();
    private static int value = 0;

    public static void test() {
        synchronized (LOCK) {
            value += 1;
        }
    }

    public synchronized static int getValue() {
        return value;
    }

}
