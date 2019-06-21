package com.throwsnew.pattern.singleton;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * author Xianfeng <br/>
 * date 19-6-21 下午6:06 <br/>
 * Desc:
 * 使用volatile和synchronized实现单例模式，需要double-check(适用于动态字段)
 * 测试
 * 1 并发情况下 线程安全
 * 2 测试effective java中,把bean引用copy一下，会不会提高性能
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DoubleCheck {

    /**
     * 必须使用volatile，避免重排序导致singletonBean已经初始化但是对其他线程不可见。
     */
    //private Bean singletonBean;//不能这样：
    private volatile Bean singletonBean;
    @Param({"100", "1000", "10000"})
    private int n;

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(DoubleCheck.class.getSimpleName())
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(3)
                .threads(1)
                .build();
        new Runner(opt).run();
    }

    private Bean getSingletonBean() {
        //(first check) 当singletonBean初始化之后，可以减少线程竞争
        if (singletonBean == null) {
            System.out.println(Thread.currentThread().getName());
            synchronized (this) {
                //(second check) 避免第二个线程获得锁之后，再次初始化
                if (singletonBean == null) {
                    singletonBean = new Bean(Thread.currentThread().getName(), true);
                }
            }
        }
        return singletonBean;
    }

    /**
     * 用新对象result 复制singletonBean
     *
     * @return singletonBean
     */
    private Bean getSingletonBeanWithCopyRef() {
        Bean result = singletonBean;
        if (result == null) {
            synchronized (this) {
                result = singletonBean;
                if (result == null) {
                    singletonBean = result = new Bean(Thread.currentThread().getName());
                }
            }
        }
        return result;
    }

    @Test
    public void concurrentTest() throws InterruptedException {
        DoubleCheck doubleCheck = new DoubleCheck();
        Thread t1 = new Thread(() -> {
            Thread.yield();
            Bean bean = doubleCheck.getSingletonBean();
            System.out.println(bean);
        });
        Thread t2 = new Thread(() -> {
            Bean bean = doubleCheck.getSingletonBean();
            System.out.println(bean);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @Benchmark
    public void testGetSingletonBean() {
        DoubleCheck doubleCheck = new DoubleCheck();
        for (int i = 0; i < n; i++) {
            Bean bean = doubleCheck.getSingletonBean();
            bean = null;
        }

    }

    @Benchmark
    public void testGetSingletonBeanWithCopyRef() {
        DoubleCheck doubleCheck = new DoubleCheck();
        for (int i = 0; i < n; i++) {
            Bean bean = doubleCheck.getSingletonBeanWithCopyRef();
            bean = null;
        }

    }
}
