package jvm.bytecode;

/**
 * author Xianfeng <br/>
 * date 19-6-18 下午5:21 <br/>
 * Desc:
 * jvm字节码测试：
 * try-catch-finally的字节码表现为异常表
 * 用非主流的代码测试try-catch-finally实际执行过程
 */
class ExceptionTableTest {

    boolean causeException = false;
    int x = 0;

    int inc() {
        try {
            x = 1;
            if (causeException) {
                throw new RuntimeException();
            }
            return x;
        } catch (Exception e) {
            x = 2;
            return x;
        } finally {
            x = 3;
            //finally这里要是return了，就始终返回3
//            return x;
        }
    }


}
