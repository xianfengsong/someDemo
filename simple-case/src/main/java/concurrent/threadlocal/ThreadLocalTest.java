package concurrent.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 2020/10/28 下午3:57 <br/>
 * Desc:
 */
public class ThreadLocalTest {

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**
     * 测试ThreadLocal让共享对象counter的属性threadLocal为线程隔离，而noThreadLocal被线程共享
     */
    @Test
    public void testShare() throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> {
            counter.init();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.add();
            System.out.println("t1:" + counter.get() + " notLocal:" + counter.getNoThreadLoad());
            Assert.assertEquals(counter.get(), 1);
            Assert.assertNotEquals(counter.getNoThreadLoad(), 3);
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
            System.out.println("t2:" + counter.get() + " notLocal:" + counter.getNoThreadLoad());
            Assert.assertEquals(counter.get(), 3);
            Assert.assertNotEquals(counter.getNoThreadLoad(), 3);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    /**
     * 测试在共享对象counter内部使用线程池修改threadLocal变量
     */
    @Test
    public void testInnerShare() throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> {
            counter.init();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.addAsync();
            System.out.println("t1:" + counter.get() + " notLocal:" + counter.getNoThreadLoad());
            Assert.assertEquals(counter.get(), 1);
            Assert.assertNotEquals(counter.getNoThreadLoad(), 3);
        });
        Thread t2 = new Thread(() -> {
            counter.init();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.addAsync();
            counter.addAsync();
            System.out.println("t2:" + counter.get() + " notLocal:" + counter.getNoThreadLoad());
            Assert.assertEquals(counter.get(), 1);
            Assert.assertNotEquals(counter.getNoThreadLoad(), 3);
        });
        t1.start();
        t1.join();
    }

    static class Counter {

        private final ThreadLocal<AtomicInteger> threadLocal;
        private AtomicInteger noThreadLocal;

        public Counter() {
            this.threadLocal = new ThreadLocal<>();
        }

        public void init() {
            threadLocal.set(new AtomicInteger(0));
            noThreadLocal = new AtomicInteger(0);
        }

        public void add() {
            threadLocal.get().incrementAndGet();
            noThreadLocal.incrementAndGet();
        }

        public void addAsync() {
            AtomicInteger share = threadLocal.get();
            Future<Integer> result = executorService.submit(() -> {
                //threadLocal又共享给其他线程
                Thread.sleep(100L);
                Assert.assertNull("threadLocal共享给其他线程时，内容无法共享", threadLocal.get());
                return share.incrementAndGet();
            });
            try {
                System.out.println("addAsync result=" + result.get());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public int get() {
            return threadLocal.get().get();
        }

        public int getNoThreadLoad() {
            return noThreadLocal.get();
        }
    }
}
