package concurrent.forkjoinpool;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-13 下午2:39 <br/>
 * Desc: 使用fork/join pool
 */
public class ForkJoinPoolTest {

    private ForkJoinPool commonPool = ForkJoinPool.commonPool();

    /**
     * 打印默认common ForkJoinPool的配置，系统配置为空，使用的是java自动计算的参数
     */
    @Test
    public void printDefaultConfig() {
        String pp = System.getProperty
                ("java.util.concurrent.ForkJoinPool.common.parallelism");
        String fp = System.getProperty
                ("java.util.concurrent.ForkJoinPool.common.threadFactory");
        String hp = System.getProperty
                ("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
        System.out.println(
                String.format("System Config:\r\nparallelism:%s\r\nthreadFactory:%s\r\nexceptionHandler:%s\r\n", pp, fp,
                        hp));
        pp = String.valueOf(commonPool.getParallelism());
        fp = commonPool.getFactory().toString();
        hp = Optional.ofNullable(commonPool.getUncaughtExceptionHandler()).map(Object::toString).orElse("null");
        System.out.println(
                String.format("Using Config:\r\nparallelism:%s\r\nthreadFactory:%s\r\nexceptionHandler:%s", pp, fp,
                        hp));
    }

    /**
     * 测试并行流/串行流
     * java8 的并行流默认使用fork-join-pool中的线程
     */
    @Test
    public void testParallelSum() {
        long sum = 0;
        for (int i = 0; i < 10; i++) {
            StreamTest streamTest = new StreamTest(LongStream.range(1, 1000_0000 * i));
            long start = System.nanoTime();
            streamTest.parallelSum();
            long time = (System.nanoTime() - start) / 1000_000;
            sum += time;
            System.out.println("parallelSum time:" + time);
        }
        System.out.println("avg time:" + (double) sum / 10);
    }

    /**
     * 测试并行流/串行流
     * java8 的并行流默认使用fork-join-pool中的线程
     */
    @Test
    public void testSerialSum() {
        long sum = 0;
        for (int i = 0; i < 10; i++) {
            StreamTest streamTest = new StreamTest(LongStream.range(1, 1000_0000 * i));
            long start = System.nanoTime();
            streamTest.serialSum();
            long time = (System.nanoTime() - start) / 1000_000;
            sum += time;
            System.out.println("serialSum time:" + time);
        }
        System.out.println("avg time:" + (double) sum / 10);
    }

    /**
     * 测试 stream 中出现异常，会中断未执行的任务
     */
    @Test
    public void testExceptionStream() {
        try {
            StreamTest streamTest = new StreamTest(LongStream.range(1, 200));
            long result = streamTest.exceptionParallelSum();
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试并行流，合并线程结果
     */
    @Test
    public void testStream() {
        long start = System.nanoTime();
        StreamTest streamTest = new StreamTest(LongStream.range(1, 100));
        String result = String.join(",", streamTest.parallelFilter());
        long time = (System.nanoTime() - start) / 1000_000;
        System.out.println("time:" + time + result);
    }

    /**
     * 测试使用无返回值的任务
     */
    @Test
    public void testRecursiveAction() {
        commonPool.invoke(new UpperStringAction("abcdefgh"));
    }

    /**
     * 测试使用有返回值的任务
     */
    @Test
    public void testRecusiveTask() {
        SumIntegerTask task = new SumIntegerTask(new int[]{1, 1, 1, 1, 1, 1, 1, 1});
        commonPool.execute(task);
        int result = task.join();
        Assert.assertEquals("结果错误", 8, result);
    }

}
