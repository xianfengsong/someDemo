package io;

import static io.CommonConstants.CLIENT_MODE;
import static io.CommonConstants.SERVER_MODE;

import io.niotest.aio.AIOClient;
import io.niotest.aio.AIOServer;
import io.niotest.clients.NIOClient;
import io.niotest.reactor.multiple.MultiReactor;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * nio reactor模式 性能测试
 * @author xianfeng
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ReactorBenchmarkTest {

    @Setup
    public void startServer() {
        try {
            Thread server = new Thread(new MultiReactor(CommonConstants.DEFAULT_PORT));
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReactorBenchmarkTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(10)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void echo() {
        try {
            NIOClient client = new NIOClient();
            Thread thread = new Thread(client);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * aio模式
     */
    private static void aio(String[] args){
        try {
            String arg = args[0];
            if (SERVER_MODE.equals(arg)) {
                Thread server = new Thread(new AIOServer());
                server.start();
            } else if (CLIENT_MODE.equals(arg)) {
                ExecutorService executor= Executors.newFixedThreadPool(20);
                for(int i=0;i<20;i++){
                    executor.execute(new AIOClient());
                }
                executor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
