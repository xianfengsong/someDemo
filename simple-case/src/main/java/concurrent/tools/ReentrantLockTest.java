package concurrent.tools;

import concurrent.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁测试
 * 1.测试公平和非公平
 * 查看等待队列信息,按入队顺序打印，观察等的久线程，是否一定先获得锁
 */
public class ReentrantLockTest implements Test {

    ReentrantLockNew fairLock = new ReentrantLockNew(true);
    //默认就是unfair
    ReentrantLockNew unfairLock = new ReentrantLockNew();
    Condition conditionF = fairLock.newCondition();


    private final static int RUNNER_NUMBER = 20;


    private void startThread(boolean useFairLock) throws InterruptedException{
        if(useFairLock){
            System.out.println("公平锁：");
        }else{
            System.out.println("非公平锁：");
        }
        //cache pool启动更快 增加线程竞争
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < RUNNER_NUMBER; i++) {
            if(useFairLock){
                Runner r = new Runner(fairLock, i);
                exec.execute(r);
            }else {
                Runner r = new Runner(unfairLock, i);
                exec.execute(r);
            }
        }
        exec.shutdown();
        exec.awaitTermination(100L, TimeUnit.SECONDS);
        exec=null;
        System.gc();
    }
    public void test() throws InterruptedException {
        startThread(false);

        startThread(true);


        System.exit(0);
    }


    class Runner implements Runnable {
        private ReentrantLockNew lock;
        private int id;


        Runner(ReentrantLockNew lock, int id) {
            this.lock = lock;
            this.id = id;

        }
        void getLock(){
            lock.lock();
            try {

                System.out.println("lock by "+Thread.currentThread().getId()+" wait:"+lock.getQueuedThreadIds());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void run() {
            //线程释放后 再次尝试获取锁 增加竞争（一般释放锁的线程容易再次获得锁）
            getLock();
            getLock();
        }
    }

    /**
     * 为了顺序打印等待队列中的线程
     */
    class ReentrantLockNew extends ReentrantLock{
        public ReentrantLockNew(){
            super();
        }

        public ReentrantLockNew(boolean isFair){
            super(isFair);
        }
        public Collection<Long> getQueuedThreadIds(){
            List<Thread> list=new ArrayList<Thread>(super.getQueuedThreads());
            Collections.reverse(list);
            List<Long> id=new ArrayList<Long>();
            for(Thread t:list){
                id.add(t.getId());
            }
            return id;
        }
    }
}
