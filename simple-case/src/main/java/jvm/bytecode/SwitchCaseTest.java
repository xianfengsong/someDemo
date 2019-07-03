package jvm.bytecode;

/**
 * author Xianfeng <br/>
 * date 19-6-18 下午6:08 <br/>
 * Desc:
 * test():
 * 从字节码看出switch-case中的break 使用的goto指令跳转到return指令
 * 如果没写break,则会继续向下执行，value=1时会打印 1,2
 * testString():
 * 从字节码看出switch String value时，会计算string的hashcode，根据hashcode比较
 * 所以传入null时，会出现空指针异常
 */
public class SwitchCaseTest {

    /**
     * 注意字节码内容：
     * 5 invokevirtual #2 <java/lang/String.hashCode>
     */
    public static void testString(String param) {
        switch (param) {
            // 肯定不是进入这里
            case "sth":
                System.out.println("it's sth");
                break;
            // 也不是进入这里
            case "null":
                System.out.println("it's null");
                break;
            // 也不是进入这里
            default:
                System.out.println("default");
        }
    }

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
