package jvm.bytecode;

/**
 * author Xianfeng <br/>
 * date 19-6-18 下午6:08 <br/>
 * Desc:
 * 从字节码看出switch-case中的break 使用的goto指令跳转到return指令
 * 如果没写break,则会继续向下执行，value=1时会打印 1,2
 */
public class SwitchCaseTest {

    public static void test(int value) {
        switch (value) {
            case 1:
                System.out.println(1);
            case 2:
                System.out.println(2);
                break;
            case 3:
                System.out.println(3);
        }
    }
}
