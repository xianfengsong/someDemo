package concurrent.futuretest;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 2020/12/23 下午4:34 <br/>
 * Desc:
 */
public class SimpleFutureTest {

    ExecutorService executor = Executors.newFixedThreadPool(2);

    @Test
    public void get() {
        Future<String> future = executor.submit(new Task());
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class Task implements Callable<String> {

        @Override
        public String call() throws Exception {
            UUID uuid = UUID.randomUUID();
            Thread.sleep(Math.abs(uuid.hashCode() % 100));
            return uuid.toString();
        }
    }
}
