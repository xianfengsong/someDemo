package concurrent.collections;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-21 下午2:08 <br/>
 * Desc:
 * CurrentHashMap函数测试
 */
public class ConcurrentHashMapTest {

    CyclicBarrier cb = new CyclicBarrier(100, new Runnable() {
        @Override
        public void run() {
            System.out.println("开始执行");
        }
    });
    private ConcurrentHashMap<String, Task> finishTaskMap = new ConcurrentHashMap<>();

    /**
     * 用ConcurrentHashMap保存任务，避免并发情况下重复执行任务
     */
    @Test
    public void testAtomicPut() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            String name = String.valueOf("0");
            pool.submit(new Task(name));
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
    }

    /**
     * computeIfAbsent 如果不存在 k/v ,根据函数用k计算一个value,保存到map
     * 测试：
     * 1.这个操作是原子的，不会重复创建
     * 2.compute函数执行时，会阻塞其他修改ConcurrentHashMap结构的操作（和操作的key无关）
     */
    @Test
    public void testComputeIfAbsent() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.submit(() -> {
                try {
                    cb.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                Task t = finishTaskMap.computeIfAbsent(String.valueOf(finalI % 5), s -> {
                    try {
                        System.out.println("start compute task, key=" + s);
                        //增加compute时间阻塞其他操作
                        Thread.sleep(1000L);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new Task(String.valueOf(finalI));
                });
                //只有compute不返回null,返回值不为空
                Assert.assertNotNull(t);
            });
        }

        //让computeIfAbsent先执行，然后put操作会被阻塞
        Thread.sleep(100L);
        //不管put的key是否存在都会阻塞
//        String key="1";
        String key = "new";
        Long start = System.currentTimeMillis();
        finishTaskMap.put("1", new Task("main"));
        System.out.println("main thread put key " + key + " ,time:" + (System.currentTimeMillis() - start));

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);


    }

    class Task implements Runnable {

        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            try {
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            if (finishTaskMap.putIfAbsent(name, this) == null) {
                System.out.println("run task " + name);
                Thread.yield();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    finishTaskMap.remove(name, this);
                    e.printStackTrace();
                }
            } else {
                System.out.println("finished task " + name);
            }

        }
    }

}
