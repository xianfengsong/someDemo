package concurrent;

import java.util.concurrent.BrokenBarrierException;

/**
 * 每个被测试类需要的接口
 */
public interface Test {

    public void test() throws BrokenBarrierException, InterruptedException;
}
