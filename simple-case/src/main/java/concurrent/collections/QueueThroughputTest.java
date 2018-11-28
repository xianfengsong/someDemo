package main.java.concurrent.collections;

import concurrent.Test;

import java.util.AbstractQueue;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 比较各个队列的性能
 */
public class QueueThroughputTest implements Test {

    static final int THREAD_NUMBER = 50;
    static final int TEST_RUN_TIMES = 100;
    static final int LOOP_PER_THREAD = 1000;

    static final long PRODUCE_TIME_SPAN = 10L;
    static final long CONSUME_TIME_SPAN = 100L;

    static final String SOME_DATA = UUID.randomUUID().toString();
    static final int CAPACITY = THREAD_NUMBER * LOOP_PER_THREAD;


    class NonBlockProducer implements Runnable{
        private Queue<String> q=null;
        public NonBlockProducer(Queue<String> q){
            this.q=q;
        }
        public void run() {
            Long threadId = Thread.currentThread().getId();

            try {
                for (int i = 0; i < LOOP_PER_THREAD; i++) {
                    q.offer(SOME_DATA + " - " + threadId + " - " + i);
                }
                Thread.sleep(PRODUCE_TIME_SPAN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    class Producer implements Runnable {
        private BlockingQueue<String> bq=null;
        public Producer(BlockingQueue<String> bq){
            this.bq=bq;
        }
        public void run() {
            Long threadId = Thread.currentThread().getId();

            try {
                for (int i = 0; i < LOOP_PER_THREAD; i++) {
                    bq.put(SOME_DATA + " - " + threadId + " - " + i);
                }
                Thread.sleep(PRODUCE_TIME_SPAN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Consumer implements Runnable {

        public void run() {

        }
    }

    class Element {

    }

    public void testPut(String type) throws InterruptedException {
        BlockingQueue<String> q=null;
        long s = System.nanoTime();
        for (int j = 0; j < TEST_RUN_TIMES; j++) {

            if(type.equals("ArrayBlockingQueue")){
                q = new ArrayBlockingQueue<String>(CAPACITY);
            }
            if(type.equals("LinkedBlockQueue")){
                q = new LinkedBlockingQueue<String>(CAPACITY);
            }
            ExecutorService service = Executors.newFixedThreadPool(THREAD_NUMBER);
            Producer p = new Producer(q);
            for (int i = 0; i < THREAD_NUMBER; i++) {
                service.execute(p);
            }
            service.shutdown();
            service.awaitTermination(1, TimeUnit.DAYS);
            service=null;
        }
        long e = System.nanoTime();
        System.out.println(type+": avg() " + (e - s) / 1000000 / TEST_RUN_TIMES + " ms");
    }
    public void testOffer(String type) throws InterruptedException {
        Queue<String> q=null;
        long s = System.nanoTime();
        for (int j = 0; j < TEST_RUN_TIMES; j++) {

            if(type.equals("ConcurrentLinkedQueue")){
                q = new ConcurrentLinkedQueue<String>();
            }
            ExecutorService service = Executors.newFixedThreadPool(THREAD_NUMBER);
            NonBlockProducer p = new NonBlockProducer(q);
            for (int i = 0; i < THREAD_NUMBER; i++) {
                service.execute(p);
            }
            service.shutdown();
            service.awaitTermination(1, TimeUnit.DAYS);
            service=null;
        }
        long e = System.nanoTime();
        System.out.println(type+": avg() " + (e - s) / 1000000 / TEST_RUN_TIMES + " ms");
    }
    public void test() throws BrokenBarrierException, InterruptedException {
        System.out.println("put test 时间间隔 ms "+PRODUCE_TIME_SPAN);

        testPut("ArrayBlockingQueue");
        testPut("LinkedBlockQueue");

        System.out.println("offer test 时间间隔 ms "+PRODUCE_TIME_SPAN);
        testOffer("ConcurrentLinkedQueue");

    }
}
