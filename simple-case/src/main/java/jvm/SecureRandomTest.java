package jvm;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 测试当前jdk版本的随机数生成
 * 使用的阻塞方式，非阻塞方式的性能区别
 * 参考 http://ifeve.com/jvm-random-and-entropy-source/
 *
 * （使用/./ 是为了避免jdk1.6 bug，1.7版本解决了
 *  参考https://bugs.java.com/view_bug.do?bug_id=6521844）
 * vm option:
 * 非阻塞  -Djava.security.egd=file:/dev/./urandom
 *        -Djava.security.egd=file:/dev/urandom
 * 阻塞  -Djava.security.egd=file:/dev/./random
 *      -Djava.security.egd=file:/dev/random
 *
 */
public class SecureRandomTest {
    public static void main(String []args) throws NoSuchAlgorithmException {
        Long s=System.currentTimeMillis();
        Long val=0L;
        for(int i=0;i<100000;i++) {
            val = SecureRandom.getInstance("SHA1PRNG").nextLong();
        }
        Long e=System.currentTimeMillis();
        System.out.println("Ok:"+(e-s)+" val:"+val);
        /**
         * 结果
         * Windows区别不明显 urandom 748ms random 795ms
         */
    }
}
