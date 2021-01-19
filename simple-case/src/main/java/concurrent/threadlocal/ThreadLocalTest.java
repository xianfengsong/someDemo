package concurrent.threadlocal;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author Xianfeng <br/>
 * date 2020/10/28 下午3:57 <br/>
 * Desc:
 */
public class ThreadLocalTest {

    @Test
    public void test() throws InterruptedException {
        Counter counter = new Counter();

        Thread t1 = new Thread(() -> {
            counter.init();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.add();
            System.out.println("t1:" + counter.get());
        });
        Thread t2 = new Thread(() -> {
            counter.init();
            counter.add();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.add();
            counter.add();
            System.out.println("t2:" + counter.get());
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    class Counter {

        private final ThreadLocal<AtomicInteger> threadLocal;

        public Counter() {
            this.threadLocal = new ThreadLocal<>();
        }

        public void init() {
            threadLocal.set(new AtomicInteger(0));
        }

        public void add() {
            threadLocal.get().incrementAndGet();
        }

        public int get() {
            return threadLocal.get().get();
        }
    }
}
