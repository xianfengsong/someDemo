package concurrent.collections;

import concurrent.Test;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018-3-19 0019.
 */
public class ConcurrentLinkQueueTest implements Test {

    private static final int THREAD_NUM = 20;

    private volatile boolean writeFinish = false;
    ConcurrentLinkedQueue<Node> q = new ConcurrentLinkedQueue<Node>();
    Map<Node, String> map = new ConcurrentHashMap<Node, String>();
    AtomicInteger total = new AtomicInteger(0);

    class Writer implements Runnable {
        public void run() {
            try {
                long threadId = Thread.currentThread().getId();
                int loop = (int) threadId % 10;

                while (loop >= 0) {

                    loop--;
//                    List<Node> nodeList = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        Node n = new Node(threadId, UUID.randomUUID().toString());
//                        nodeList.add(n);
                        q.offer(n);
                        total.incrementAndGet();
                    }
                    //使用addAll会导致漏读,addAll不保证原子性
//                    q.addAll(nodeList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Reader implements Runnable {
        private final long SLEEP_TIME=100L;
        public void run() {
            while (!writeFinish) {
                Node n = q.poll();
                if (n != null) {
                    map.put(n, "");
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //writeFinish和poll不是原子操作
            //poll结束 和 读writeFinish=true 的操作之间，可能q又被更新
            //(增加SLEEP_TIME复现bug)
            if(!q.isEmpty()){
                Node n;
                while((n=q.poll())!=null){
                    map.put(n,"");
                }
            }
        }
    }

    public void test() throws BrokenBarrierException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i++) {
            executorService.execute(new Writer());
        }
        ExecutorService readExec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            readExec.execute(new Reader());
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        writeFinish = true;
        readExec.shutdown();
        readExec.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("total:" + total.get());
        if (total.get() != map.size()) {
            System.out.println("size:" + map.size());
            System.out.print("处理失败");
        } else {
            System.out.print("处理成功");
        }
    }

    class Node {
        long threadId;
        String id;

        public Node(long threadId, String id) {
            this.threadId = threadId;
            this.id = id;
        }
    }
}
