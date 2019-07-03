package concurrent.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 测试非线程安全集合，在并发时的问题
 */
public class UnThreadsafeCollectionTest {

    public static Map<Integer, String> map = new HashMap<Integer, String>(16, 0.9f);

    /**
     * 测试多个线程向hashmap放入元素 key不重复
     * 结果：
     * 1.出现null value的元素（value被覆盖）
     * (当线程放入的key重复时，null出现的更多)
     * 2.增大load factor之后 出现某个线程放入的数据全部为null
     * (扩容时丢失)
     */
    public static void hashMapTest() {
        ExecutorService exec = Executors.newCachedThreadPool();
        Runner one = new Runner(0, 2000);
        Runner two = new Runner(2001, 3000);
        Runner three = new Runner(3001, 4000);
        Runner four = new Runner(4001, 5000);
        Runner five = new Runner(5001, 6000);
        exec.execute(one);
        exec.execute(two);
        exec.execute(three);
        exec.execute(four);
        exec.execute(five);
        try {
            exec.shutdown();
            exec.awaitTermination(200, TimeUnit.SECONDS);
            for (int i = 0; i <= 6000; i++) {
                System.out.println(i + ":" + map.get(i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Runner implements Runnable {

        int minKey, maxKey;

        public Runner(int minKey, int maxKey) {
            this.maxKey = maxKey;
            this.minKey = minKey;
        }

        public void run() {
            String name = Thread.currentThread().getName();
            System.out.println(name);
            for (int i = minKey; i <= maxKey; i++) {
                map.put(i, name);
                Thread.yield();
            }
        }
    }

}
