package generic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * author Xianfeng <br/>
 * date 19-6-20 下午3:47 <br/>
 * Desc:
 * toArray时不需要指定数组大小，这样就好 toArray(new Long[0])，耗时更短(一点点)
 * 而且并发情况下toArray(new Long[a.size()])写法，a.size()会被改变，导致预设的数组大小和需要的不符
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class ToArrayBenchmark {

    private final List<Object> list = new ArrayList<>();
    @Param({"100", "1000", "10000", "100000"})
    private int n;

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(ToArrayBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(3)
                .threads(1)
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void populateList() {
        for (int i = 0; i < n; i++) {
            list.add(0);
        }
    }

    @Benchmark
    public Object[] preSize() {
        return list.toArray(new Object[n]);
    }

    @Benchmark
    public Object[] resize() {
        return list.toArray(new Object[0]);
    }

}
