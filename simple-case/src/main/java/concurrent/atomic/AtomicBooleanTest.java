package concurrent.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-21 上午9:36 <br/>
 * Desc:
 */
public class AtomicBooleanTest {

    private AtomicBoolean hasDone = new AtomicBoolean(false);

    public void doSomething(int i) {
        if (hasDone.compareAndSet(false, true)) {
            System.out.println("done");
            try {
                if (i < 10) {
                    wrong();
                }
            } catch (Exception e) {
                hasDone.set(false);
            }
        } else {
            System.out.println("skip");
        }
    }

    private void wrong() {
        throw new RuntimeException();
    }

    @Test
    public void test() {
        ExecutorService pool = Executors.newFixedThreadPool(50);
        AtomicBooleanTest test = new AtomicBooleanTest();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.execute(() -> test.doSomething(finalI));
        }
    }
}
