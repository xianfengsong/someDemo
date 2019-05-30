package guava.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * author Xianfeng <br/>
 * date 19-5-27 上午10:26 <br/>
 * Desc:
 * 测试开启recordStats会不会影响guava cache性能
 * group cache : 不开recordStats,cache同时读写的耗时
 * group rcache: 开recordStats,cache同时读写的耗时
 */
@State(Scope.Group)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CacheBenchMark {

    private Cache<String, String> cache, recordCache;
    private AtomicInteger recordCacheKey = new AtomicInteger(0);
    private AtomicInteger cacheKey = new AtomicInteger(0);

    public CacheBenchMark() {
        cache = CacheBuilder.newBuilder().maximumSize(1000).build();
        recordCache = CacheBuilder.newBuilder().maximumSize(1000).recordStats().build();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(CacheBenchMark.class.getSimpleName())
                .forks(4)
                .warmupIterations(1)
                .measurementIterations(4)
                .build();
        new Runner(options).run();
    }

    @Benchmark
    @Group(value = "cache")
    public void setCache() {
        cache.put("Cache" + cacheKey.incrementAndGet(), UUID.randomUUID().toString());
    }

    @Benchmark
    @Group(value = "cache")
    public void getCache() {
        cache.getIfPresent("Cache" + System.currentTimeMillis() % Math.max(cacheKey.get(), 1));
    }

    @Benchmark
    @Group(value = "rcache")
    public void setRecordCache() {
        recordCache.put("RCache" + recordCacheKey.incrementAndGet(), UUID.randomUUID().toString());
    }

    @Benchmark
    @Group(value = "rcache")
    public void getRecordCache() {
        cache.getIfPresent("RCache" + System.currentTimeMillis() % Math.max(recordCacheKey.get(), 1));
    }


}
