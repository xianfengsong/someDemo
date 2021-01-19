package concurrent.atomic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 2020/10/26 下午8:57 <br/>
 * Desc:
 */
public class AtomicLongTest {

    private final AtomicLong aLong = new AtomicLong(Long.MAX_VALUE);
    private Random random = new Random();

    @Test
    public void testSet() throws InterruptedException {
        List<Long> ids = Arrays.asList(100L, 101L, 102L, 103L);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (Long id : ids) {
            if (id < aLong.get()) {
                aLong.set(id);
            }
            executorService.execute(() -> {
                try {
                    Thread.sleep(random.nextInt(1000));
                    while (id != aLong.get()) {
                        System.out.println("wait... id=" + id + " aLong=" + aLong.get());
                        Thread.sleep(100L);
                    }
                    aLong.incrementAndGet();
                    System.out.println("done id=" + id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println(aLong.get());

    }
}
