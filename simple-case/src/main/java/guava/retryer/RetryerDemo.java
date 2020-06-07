package guava.retryer;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-7-22 上午9:58 <br/>
 * Desc:
 * 测试一个基于guava扩展的开源的重试工具，支持定义重试条件(根据异常类型/返回结果)，支持retryListener
 * https://github.com/rholder/guava-retrying
 */
public class RetryerDemo {



    /**
     * 重试器
     */
    Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
            .retryIfResult(Objects::isNull)
            .retryIfExceptionOfType(IOException.class)
            .retryIfRuntimeException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(2))
            .build();
    //远程调用 依赖其他服务
    private Callable<Boolean> remoteCall;

    /**
     * 测试 达到最大重试次数后，返回异常(包含第一调用)
     *
     * @throws RetryException 重试都失败抛出RetryException
     */
    @Test(expected = RetryException.class)
    public void failWithRetryException() throws RetryException {
        try {
            remoteCall = () -> {
                System.out.println("call--" + Thread.currentThread().getId());
                throw new RuntimeException();
            };
            System.out.println("retryer--" + Thread.currentThread().getId());
            retryer.call(remoteCall);
        } catch (ExecutionException e) {
            //callable返回异常但是没有触发重试，抛出ExecutionException
            e.printStackTrace();
        } catch (RetryException e) {
            //重试后仍然失败，抛出这个RetryException异常
            System.out.println("retryer重试后仍然失败:" + e.getMessage());
            throw e;
        }
    }

    /**
     * 测试通过Listener获得每次调用的信息
     */
    @Test
    public void testRetryListener() {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(r -> Objects.equals(r, false))
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterDelay(5, TimeUnit.MILLISECONDS))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        try {
                            System.out.println("\r\n-----");
                            //Attempt封装了一次调用的结果和统计信息
                            if (attempt.hasResult()) {
                                System.out.println("调用结果 :" + attempt.get().toString());
                            }
                            System.out.println("重试次数 :" + attempt.getAttemptNumber());
                            System.out.println("已重试时间：" + attempt.getDelaySinceFirstAttempt());
                            if (attempt.hasException()) {
                                System.out.println("调用的异常：" + attempt.getExceptionCause());
                            }

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).build();
        try {
            boolean result = retryer.call(() -> {
                if (System.currentTimeMillis() % 2 == 0) {
                    throw new RuntimeException();
                }
                return false;
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试callable执行异常，但是没有触发重试，retryer会抛出ExecutionException
     */
    @Test(expected = ExecutionException.class)
    public void testExecutionException() throws ExecutionException, RetryException {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        //只执行了一次。。 因为没命中重试策略就不会重试
        retryer.call(() -> {
            System.out.println("call");
            throw new RuntimeException();
        });
    }

    /**
     * 测试callable执行异常触发重试，如果重试最大次数之后还是失败，retryer会抛出RetryException
     */
    @Test(expected = RetryException.class)
    public void testRetryException() throws ExecutionException, RetryException {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //让它重试
                .retryIfRuntimeException()
                .build();
        retryer.call(() -> {
            System.out.println("call");
            throw new RuntimeException();
        });
    }

}
