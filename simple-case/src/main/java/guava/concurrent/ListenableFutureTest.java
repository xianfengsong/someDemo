package guava.concurrent;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * author Xianfeng <br/>
 * date 2020/12/24 下午2:44 <br/>
 * Desc:
 */
public class ListenableFutureTest {

    private final ExponentialBackOff backOff = new ExponentialBackOff(100, 2.0d);
    AtomicInteger offset = new AtomicInteger(0);

    @Test
    public void testFinishByOrder() throws InterruptedException {
        backOff.setMaxElapsedTime(12800L);
        BackOffExecution retry = backOff.start();
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4));
        ExecutorService callbackPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            ListenableFuture<Integer> future = pool.submit(new Task(i));
            future.addListener(() -> {
                try {
                    Integer id = future.get();
                    while (!offset.compareAndSet(id, id)) {
                        long wait = retry.nextBackOff();
                        if (BackOffExecution.STOP == wait) {
                            if (id - offset.get() < 10) {
                                int newVal = offset.updateAndGet(operand -> id);
                                System.out.println("commit with gap,id=" + id + " offset=" + newVal);
                            } else {
                                System.out.println("wait timeout,id=" + id);
                            }
                            break;
                        } else {
                            Thread.sleep(wait * id);
                        }
                    }
                    System.out.println("task " + id + " finish");
                    offset.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }, callbackPool);
        }
        Thread.sleep(100000L);
    }

    class Task implements Callable<Integer> {

        final int id;

        Task(int id) {
            this.id = id;
        }

        @Override
        public Integer call() throws Exception {
            Random r = new Random(System.nanoTime());
            Thread.sleep(r.nextInt(1000));
            return id;
        }
    }
}
