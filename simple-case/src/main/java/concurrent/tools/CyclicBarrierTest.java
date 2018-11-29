package concurrent.tools;

import concurrent.Test;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Cyclic Barrier测试
 * 模拟一个比赛过程,设置cb的线程数是参赛人数，action是比赛即将开始时执行的
 * cb的作用是 阻塞每个线程一直到所有线程都调用了await()
 * 通过cb.reset可以继续使用cb
 */
public class CyclicBarrierTest implements Test {
    private final int NUMBER = 10;
    CyclicBarrier cb = new CyclicBarrier(NUMBER, new AllReadyAction());

    public void test() throws BrokenBarrierException, InterruptedException {
        while (true) {
            cb.reset();
            ExecutorService ex = Executors.newFixedThreadPool(NUMBER);
            for (int i = 0; i < NUMBER; i++) {
                ex.execute(new Runner());
            }
            Thread.sleep(1000L);
            ex.shutdownNow();
            ex.awaitTermination(1,TimeUnit.DAYS);
            System.out.println("比赛结束\r\n");
        }

    }

    class Runner implements Runnable {
        public void run() {
            Long id = Thread.currentThread().getId();

            String path=id.toString();
            try {
                Long tryInterrupt=ThreadLocalRandom.current().nextLong(1,100);
                //测试中断对cb的影响
                if(tryInterrupt>90){
                    Thread.currentThread().interrupt();
                    path+=" 提出退赛！";
                }
                //测试超时对cb的影响
                int index = cb.await(10,TimeUnit.MILLISECONDS);
                path+="("+index+")";
                if(index==0){
                    System.out.println(id+"是最后准备完成的");
                }
                while(true){
                    path+=" = ";
                    Thread.sleep(ThreadLocalRandom.current().nextLong(1,100));
                }

            } catch (InterruptedException e) {
                path += ";";
            } catch (BrokenBarrierException e) {
                path += " 退赛！ BrokenException";
            } catch (TimeoutException e) {
                path += " 超时！等待时间太长";
            } finally {
                System.out.println(path);
            }
        }
    }

    class AllReadyAction implements Runnable {
        public void run() {
            System.out.println("比赛即将开始，看谁跑的远");
        }
    }
}
