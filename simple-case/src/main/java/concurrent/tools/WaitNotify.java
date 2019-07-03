package concurrent.tools;

import concurrent.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * wait() notify()的测试
 * 当条件为true,Controller通知其他线程
 *
 * waiter进入同步块之后调用wait()
 * 因为调用wait()后线程会失去对monitor的占用
 * 所以其他线程也能够进入同步块调用wait()
 *
 * 最后controller会随机唤醒一个线程
 * 但是因为notify()不会释放monitor
 * 所以等controller同步块代码执行完成才真正唤醒
 *
 * 因此 WaiterWithTimeout 虽然wait()方法早已超时
 * 但是它失去了monitor，所以还是不会立刻结束
 *
 * 注意wait/notify的编程建议（condition的作用）
 */
public class WaitNotify implements Test {

    /**
     * java doc的建议
     * 在使用wait/notify时，wait()方法要在循环中调用(等待的条件不满足时)
     */
    private static boolean condition = false;
    private final Object lock = new Object();

    public void test() {
        Waiter waiter = new Waiter();
        WaiterWithTimeout withTimeout = new WaiterWithTimeout();
        ExecutorService exec = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 5; i++) {
            if (i == 4) {
                exec.execute(withTimeout);
            } else {
                exec.execute(waiter);
            }
        }
        exec.execute(new Controller());
        exec.shutdown();
    }

    class Controller implements Runnable {

        public void run() {
            try {
                Thread.sleep(100L);
                System.out.println("Controller start ");
                synchronized (lock) {
                    System.out.println("Controller get monitor");
                    condition = true;
                    //唤醒所有
//                    lock.notifyAll();
                    //唤醒一个
                    lock.notify();
                    //继续占用这个monitor一会
                    int i = 2;
                    while (i > 0) {
                        i--;
                        System.out.println("Controller running");
                        Thread.sleep(2000L);
                    }
                }
                System.out.println("Controller stop");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Waiter implements Runnable {

        public void run() {
            try {
                String id = Thread.currentThread().getName();
                System.out.println(id + " start");
                synchronized (lock) {

                    while (condition == false) {
                        System.out.println(id + " wait");
                        lock.wait();
                    }
                    System.out.println(id + " 收到通知");
                }
                System.out.println(id + " finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class WaiterWithTimeout implements Runnable {

        public void run() {
            try {
                String id = Thread.currentThread().getName() + "(WithTimeout)";
                System.out.println(id + " start ");
                synchronized (lock) {

                    while (condition == false) {
                        System.out.println(id + " wait");
                        lock.wait(400L);
                    }

                    System.out.println(id + " 收到通知");
                }
                System.out.println(id + " finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}