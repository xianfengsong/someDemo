package concurrent.lock;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * author Xianfeng <br/>
 * date 19-6-13 上午9:55 <br/>
 * Desc: 测试加了synchronize对性能有影响没
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SyncBenchMark {

    private long value;

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(SyncBenchMark.class.getSimpleName())
                .forks(1)
                .warmupIterations(0)
                .measurementIterations(5)
                .build();
        new Runner(options).run();
    }

    /**
     * SyncBenchMark.withoutLock:withoutLock·p0.99    sample       83.565          ms/op
     * SyncBenchMark.withoutLock:withoutLock·p0.999   sample       95.027          ms/op
     */
    @Benchmark
    public void withoutLock() {
        for (int i = 0; i < Integer.MAX_VALUE - 2; i++) {
            value = value + 1;
        }
    }

    /**
     * SyncBenchMark.withLock:withLock·p0.99    sample       66.370          ms/op
     * SyncBenchMark.withLock:withLock·p0.999   sample       66.716          ms/op
     */
    @Benchmark
    public void withLock() {
        synchronized (this) {
            for (int i = 0; i < Integer.MAX_VALUE / 2; i++) {
                value = value + 1;
            }
        }
    }

}
