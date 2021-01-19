package concurrent.threadmodel;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExitByOrder {

    @Test
    public void test() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Random random = new Random();
        int maxGap = 2;
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 100; i++) {
            final int copy = i;
            executorService.execute(() -> {
                try {
                    Thread.sleep(random.nextInt(1000));
                    //必须按顺序退出
                    long s = System.currentTimeMillis();
                    int k = 0;
                    while (copy >= counter.get() && copy - counter.get() > maxGap) {
                        Thread.sleep(100L * k);
                        k++;
                    }
                    if (copy < counter.get()) {
                        System.out.println(copy + " just exit!");
                    } else {
                        System.out.println(copy + " exit! wait=" + (System.currentTimeMillis() - s));
                    }
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("time:" + (System.currentTimeMillis() - start));
    }
}
