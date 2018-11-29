package guava.limiter;

import com.google.common.util.concurrent.RateLimiter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * guava RateLimiter 限流器测试
 */
public class RateLimiterTest {
    private final static RateLimiter limiter = RateLimiter.create(10.0d);

    /**
     * acquire阻塞直到获得令牌
     */
    private static void testBlockingAcquire() {
        double maxWaitTime = 0.0d;
        for (int i = 0; i < 50; i++) {
            //还能返回等待时间
            double waitTime = limiter.acquire();
            System.out.println(new Date().getSeconds()+"  wait:"+waitTime);
        }
    }

    /**
     * tryAcquire 立刻返回结果
     */
    private static void testTryAcquireNonBlock() {

        for (int i = 0; i < 50; i++) {
            if (limiter.tryAcquire()) {
                System.out.println(new Date().getSeconds() + " :pass");
            } else {
                System.out.println(new Date().getSeconds() + " :limit");
            }
        }
    }

    /**
     * 可以应对突发流量,但是后续请求会因此多等待一会
     * 并不会因为acquire的数量超过令牌数就拒绝分配
     */
    private static void testAcquireMany() {
        int [] acquireCount=new int[]{50,10,20,2};
        for(int i=0;i<acquireCount.length;i++){
            double waitTime = limiter.acquire(acquireCount[i]);
            System.out.println(new Date().getTime() + " wait:" + waitTime+"s get:"+acquireCount[i]);
        }
    }

    /**
     *
     */
    private static void warmUpTest(){
        RateLimiter limiter=RateLimiter.create(10.0,5, TimeUnit.SECONDS);
        for(int i=0;i<50;i++){
            double waitTime = limiter.acquire();
            System.out.println(new Date().getSeconds()+" wait:"+waitTime);
        }
    }

    public static void main(String[] args) {

        try {
            System.out.println("warm up模式,预热指定时间后(5s),达到最大速度,平均等待时间0.1s");
            warmUpTest();
            Thread.sleep(2000L);

            System.out.println("smooth模式(默认),马上达到最大速度,平均等待时间0.1s");
            testBlockingAcquire();
            System.out.println("等两秒\r\n");
//            Thread.sleep(2000L);

//            testTryAcquireNonBlock();
//            Thread.sleep(2000L);

//            testAcquireMany();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
