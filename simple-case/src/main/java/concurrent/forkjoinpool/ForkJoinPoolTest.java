package concurrent.forkjoinpool;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
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
     * 测试使用无返回值的任务
     */
    @Test
    public void testRecursiveAction() {
        commonPool.invoke(new UpperStringAction("abcdefgh"));
    }

    /**
     * 测试任务出现异常，ForkJoinPool不会退出,也不会获得异常
     * 但是action能够获得异常啊(搞什么)
     */
    @Test
    public void main() throws InterruptedException {
        UpperStringAction action = new UpperStringAction(null);

        commonPool.execute(action);
        commonPool.shutdown();
        commonPool.awaitTermination(1, TimeUnit.DAYS);

        boolean fail = action.isCompletedAbnormally();
        Assert.assertTrue("isCompletedAbnormally返回true", fail);
        Throwable exception = action.getException();
        Assert.assertEquals("java.lang.IllegalArgumentException: workload为空", exception.getMessage());

        System.out.println("任务完成");
        action.join();
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
