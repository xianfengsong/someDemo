package concurrent.threadtest;

import concurrent.Test;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by root on 18-3-15.
 */
public class ThreadTest implements Test{
    Logger logger= LogManager.getLogger();
    Lock lock=new ReentrantLock();
    Condition condition=lock.newCondition();
    class Runner implements Runnable{
        public void run() {
            try {
                logger.debug(Thread.currentThread().getId()+" sleep()!");
                //TIMED_WAITING
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //会有一个线程状态变BLOCKED
            synchronized (ThreadTest.class){
                int i=Integer.MAX_VALUE;
                while(i>0){i--;}
                logger.debug(Thread.currentThread().getId()+" enter synchronized!");
            }
            //WAITING
            lock.lock();
            logger.debug(Thread.currentThread().getId()+":i have lock");

            try {
                //WAITING
                condition.await();
                logger.debug(Thread.currentThread().getId()+":im running");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }
    class Watcher implements Runnable{
        Thread threadBeenWatch;
        Watcher(Thread thread){
            threadBeenWatch=thread;
        }
        @Override
        public void run() {
            Thread.State t=null;
            while(true){
                Thread.State now=threadBeenWatch.getState();
                if(!now.equals(t)){
                    logger.debug(threadBeenWatch.getId()+":"+now);
                    t=now;
                }
            }
        }
    }
    @Override
    public void test() throws BrokenBarrierException, InterruptedException {
        Thread thread=new Thread(new Runner());
        Thread thread1=new Thread(new Runner());

        Thread watcher=new Thread(new Watcher(thread));
        watcher.start();
        Thread watcher2=new Thread(new Watcher(thread1));
        watcher2.start();
        //等一会再 start 此时状态应该是NEW
        watcher2.join(2000L);
        logger.debug("  start()!  ");
        thread.start();
        thread1.start();

        thread1.join(1000L);
        lock.lock();
        logger.debug("  signal!  ");
        condition.signal();
        lock.unlock();
        thread.join(500L);
        lock.lock();
        logger.debug("  signal!  ");
        condition.signal();
        lock.unlock();
    }
}
