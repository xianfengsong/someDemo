package concurrent.sytemproblem.falsesharing;

/**
 * author Xianfeng <br/>
 * date 19-6-13 下午8:15 <br/>
 * Desc:
 * 测试伪共享问题对性能的影响
 * 伪共享问题、不同线程使用同一个cache line保存变量
 * 介绍：https://dzone.com/articles/false-sharing
 */
public final class FalseSharingRunner implements Runnable {

    public final static int NUM_THREADS = 1; // change
    public final static long ITERATIONS = 1000L * 1000L;
    private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];

    static {
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new VolatileLong();
        }
    }

    private final int arrayIndex;

    public FalseSharingRunner(final int arrayIndex) {
        this.arrayIndex = arrayIndex;

    }

    /**
     * 一个线程时没有区别，4个线程区别很大
     *
     * 去除伪共享(ns)
     * duration = 3552_4265
     * duration = 35270571
     * duration = 29893127
     * duration = 33463713
     * duration = 35111474
     * * 一个线程
     * * duration = 9050709
     * * duration = 7125001
     * * duration = 7617258
     *
     * 伪共享
     * duration = 6671_4293
     * duration = 62760564
     * duration = 46784825
     * duration = 52734748
     * * 一个线程
     * * duration = 9151820
     * * duration = 7623586
     * * duration = 7229062
     */
    public static void main(final String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            System.gc();
            final long start = System.nanoTime();
            runTest();
            System.out.println("duration = " + (System.nanoTime() - start));
        }
    }

    private static void runTest() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharingRunner(i));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    @Override
    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = i;
        }
    }

    public final static class VolatileLong {

        public volatile long value = 0L;
        public long p1, p2, p3, p4, p5, p6; // comment out
    }
}