package main.java.concurrent;


import main.java.concurrent.collections.CurrentLinkQueueTest;
import main.java.concurrent.collections.QueueThroughputTest;
import main.java.concurrent.collections.UnThreadsafeCollectionTest;
import concurrent.tools.*;

public class Main {
    public static void main(String[] args) {
        String type = args[0];
        try {

            if (type.equals("WaitNotify")) {
                WaitNotify waitNotify = new WaitNotify();
                waitNotify.test();
            }
            if (type.equals("Latch")) {
                CountDownLatchTest t = new CountDownLatchTest();
                t.test();
            }
            if (type.equals("CLH")) {
                LockQueueStructure clh = new LockQueueStructure();
                for (int i = 0; i < 10; i++) {
                    clh.test();
                }
            }
            if (type.equals("cb")) {
                CyclicBarrierTest cb = new CyclicBarrierTest();
                cb.test();
            }
            if(type.equals("smp")){
                SemaphoreTest t=new SemaphoreTest(true);
                t.test();
            }
            if(type.equals("thread")){
//                ThreadTest t=new ThreadTest();
//                t.test();
                ThreadClassLoaderTest threadClassLoaderTest=new ThreadClassLoaderTest();
                threadClassLoaderTest.test();
            }
            if(type.equals("hashmap")){
                UnThreadsafeCollectionTest.hashMapTest();
            }
            if(type.equals("queue")){
                CurrentLinkQueueTest test=new CurrentLinkQueueTest();
                test.test();
            }
            if(type.equals("qt")){
                QueueThroughputTest test=new QueueThroughputTest();
                test.test();
            }
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }
}
