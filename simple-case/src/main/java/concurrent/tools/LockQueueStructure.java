package concurrent.tools;

import concurrent.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * java lock的基础数据结构
 * 包括 CLH 队列锁  MCS 锁
 */
public class LockQueueStructure implements Test {

    //每个线程通过修改它 留下执行的记录
    static volatile int sharedVal = 0;

    /**
     * 由锁来控制的runner
     */
    class Runner implements Runnable {
        private Lock lock;
        private int id;

        Runner(Lock lock, int id) {
            this.lock = lock;
            this.id = id;
        }

        public void run() {

            lock.lock();
            sharedVal = (sharedVal * 10) + id;
            try {
                Thread.sleep(20L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();

        }
    }

    /**
     * 验证lock的作用
     * 能否让runner以请求锁的顺序 同步执行
     * 期待输出 123456789
     */
    public void test() {
        try {
            sharedVal=0;
            Lock lock = new CLHLock();
//            Lock lock = new MCSLock();
            int poolSize = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
            for (int i = 0; i < poolSize; i++) {
                Runnable r = new Runner(lock, i);
                //保证启动次序
                Thread.sleep(100L);
                executorService.execute(r);
            }
            executorService.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(sharedVal);
    }

    /**
     * CLH 锁队列
     * 实现了锁的公平分配，先到先得
     * 结构是一个由多个节点组成的锁队列，每个节点保存上个节点的引用
     * 头节点是当前获得锁的节点，希望获得锁的线程从队列尾部插入,从前到后依次获得锁
     * 加锁时要自旋查看上个节点的状态（大量访问），等上个节点解锁，当前节点才能完成加锁
     * 解锁时要更新本节点状态
     * 优点是：每个线程只在自己前驱节点上自旋，不会集中在同一个变量自旋
     * （避免造成大量缓存一致性操作）
     */
    class CLHLock implements Lock {

        private final ThreadLocal<Node> prev, myNode;
        private final AtomicReference<Node> tail = new AtomicReference<Node>();

        CLHLock() {
            prev = new ThreadLocal<Node>() {
                protected Node initialValue() {
                    return null;
                }
            };
            myNode = new ThreadLocal<Node>() {
                protected Node initialValue() {
                    return new Node();
                }
            };
        }

        /**
         * 更新自己状态为竞争锁（这一步会阻止后面的线程获得锁）
         * 把tail指向自己，等待prev(原来的tail)释放锁
         */
        public void lock() {

            final Node node = this.myNode.get();

            //自旋 等着把tail指向自己(加入队列)
            Node prev = tail.getAndSet(node);
            //加入队列后立即更新状态 让后面来的自旋等待
            node.waitLock = true;

            this.prev.set(prev);
            //自旋 等着前驱解锁
            while (prev != null && prev.waitLock) ;

        }

        public void unlock() {
            final Node node = this.myNode.get();
            node.waitLock = false;
            //清空当前节点
            this.myNode.set(prev.get());
        }

        /**
         * waitLock 表示节点锁定状态
         * true 表示需要锁（或已经得到锁）
         * false 表示不需要锁
         */
        class Node {
            volatile boolean waitLock;
        }

        public void lockInterruptibly() throws InterruptedException {

        }
        public boolean tryLock() {
            return false;
        }
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }
        public Condition newCondition() {
            return null;
        }
    }

    /**
     * MCS 锁队列
     * 实现了锁的公平分配，先到先得
     * 结构是一个由多个节点组成的锁队列 每个节点保存下个节点的引用
     * 头节点是当前获得锁的节点，希望获得锁的线程从队列尾部插入,从前到后依次获得锁
     *
     * 加锁时要 自旋查看本节点状态是否能获得锁
     * 解锁时要更新下个节点的状态（如果有）
     * 优点是：
     * 避免了 CLH在其他节点的状态变量上自旋，在NUMA架构CPU上表现更好
     * （避免大量访问远端内存）
     */
    class MCSLock implements Lock {

        AtomicReference<Node> tail=new AtomicReference<Node>();
        private final ThreadLocal<Node> myNode;

        public MCSLock(){
            myNode=new ThreadLocal<Node>(){
                @Override
                protected Node initialValue() {
                    return new Node();
                }
            };
        }

        public void lock() {
            Node node=myNode.get();
            /* 1 */
            //自旋直到接入队列
            Node prev=tail.getAndSet(node);
            if(prev!=null){
                node.waitLock=true;
                /* 2 */
                //让前驱指向自己
                prev.next=node;
                /* 3 */
                //等着前驱把我释放（只在自己节点变量自旋）
                while(node.waitLock);
            }
        }

        /**
         *  步骤 4 和 1 产生竞争
         *  竞争失败 步骤 5 等待  2 完成
         *  步骤 6 解除 3 的自旋
         */
        public void unlock() {
            Node node=myNode.get();
            //如果此时是最后一个节点
            if(node.next==null){
                /* 4 */
                //把tail改为null(cas防止这个瞬间还有节点接入队列)
                if(tail.compareAndSet(node,null)){
                    return;
                }
                /* 5 */
                //cas失败 （自旋，等着后面的新节点更新本节点next引用指向它）
                while (node.next==null);
            }
            /* 6 */
            //真的有next节点接入了，释放它的等待状态
            node.next.waitLock=false;
        }
        class Node{
            volatile boolean waitLock;
            volatile Node next;
        }

        public void lockInterruptibly() throws InterruptedException {

        }
        public boolean tryLock() {
            return false;
        }
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }
        public Condition newCondition() {
            return null;
        }
    }

}
