package concurrent.futuretest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 2020/10/22 下午8:11 <br/>
 * Desc:
 * 测试 CompletableFuture 的用法
 */
public class CompletableFutureTest {

    /**
     * 测试普通用法，捕获线程内异常
     */
    public Future<Integer> testNormalUsage() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        new Thread(() -> {
            Coder coder = new Coder();
            try {
                Integer result = coder.coding();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }).start();
        return future;
    }

    /**
     * 测试工厂方法创建，不用主动调用complete/completeExceptionally
     * 但是异常要转为unchecked exception
     */
    public Future<Integer> testFactoryMethod() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Coder coder = new Coder();
                return coder.coding();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public Future<Integer> customThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Coder coder = new Coder();
                return coder.coding();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            }
        }, executorService);
    }

    @Test
    public void testUsage() {
        try {
            //test normal
//            Future<Integer> future = testNormalUsage();
            //testFactoryMethod
            Future<Integer> future = testFactoryMethod();
            doOtherThing();
            System.out.println("coding! lines=" + future.get(2, TimeUnit.SECONDS));
        } catch (Exception e) {
            System.out.println("got exception from future," + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 让coding/test两组异步任务按照串行顺序执行
     */
    @Test
    public void testSerial() {
        long start = System.nanoTime();
        List<String> coders = Arrays.asList("a", "b", "c");
        List<CompletableFuture<Boolean>> futures = coders.stream().map(Coder::new)
                //coding
                .map((coder) -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return coder.coding();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }))
                //when coding done,print
                .map(future -> future.thenApply(i -> {
                    System.out.println("apply,coding done,lines=" + i);
                    return i;
                }))
                //when coding done,testing
                .map(future -> future.thenCompose(i -> CompletableFuture.supplyAsync(() -> new Tester().testing(i))))
                .collect(Collectors.toList());
        List<Boolean> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        long time = (System.nanoTime() - start) / 1000_000;
        System.out.println(results + " time=" + time);
    }

    /**
     * 通过combine 等两个异步任务都返回
     */
    @Test
    public void testParallel() throws InterruptedException, ExecutionException, TimeoutException {
        Coder a = new Coder("a");
        Coder b = new Coder("b");
        Tester c = new Tester();
        long start = System.nanoTime();
        Boolean result = CompletableFuture.supplyAsync(() -> {
            try {
                int lines = a.coding();
                System.out.println("a coding " + lines);
                return lines;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                int lines = b.coding();
                System.out.println("b coding " + lines);
                return lines;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }), (aLines, bLines) -> c.testing(aLines + bLines)).get();
        long time = (System.nanoTime() - start) / 1000_000;
        System.out.println(result + " time=" + time);
        Assert.assertTrue("时间不会超过3秒", time < 3000);
    }

    /**
     * 用错误的方式，等待多个线程结束
     * 注意运行时间和串行一样
     */
    @Test
    public void testCombineStreamWrong() {
        Long start = System.currentTimeMillis();
        List<Coder> coders = new ArrayList<Coder>() {{
            add(new Coder("1"));
            add(new Coder("2"));
            add(new Coder("3"));
        }};
        List<Integer> result = coders.stream().map(c -> CompletableFuture.supplyAsync(() -> {
            try {
                return c.coding();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        })).map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(result);
    }

    /**
     * 用正确的方式，等待多个线程结束
     * 就是有点别扭
     */
    @Test
    public void testCombineStream() {
        Long start = System.currentTimeMillis();
        List<Coder> coders = new ArrayList<Coder>() {{
            add(new Coder("1"));
            add(new Coder("2"));
            add(new Coder("3"));
        }};
        List<CompletableFuture<Integer>> futures = coders.stream().map(c -> CompletableFuture.supplyAsync(() -> {
            try {
                return c.coding();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        })).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println(System.currentTimeMillis() - start);
        List<Integer> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println(result);
    }

    private void doOtherThing() {
        System.out.println("slacking... waiting to go home");
    }

    static class Coder {

        Random random = new Random();
        private String name;

        public Coder() {
            this.name = "null";
        }

        public Coder(String name) {
            this.name = name;
        }

        public Integer coding() throws InterruptedException {
            try {
                Thread.sleep(1000L);
                return random.nextInt(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    static class Tester {

        public Boolean testing(Integer codeLines) {
            try {
                Thread.sleep(1000L);
                Boolean result = codeLines % 2 == 0;
                System.out.println("lines:" + codeLines + " result:" + result);
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    @Test
    public void insert() {
        List<String> list = new ArrayList<String>() {{
            add("b");
            add("d");
        }};
        list.add(0, "a");
        list.add(2, "c");
        System.out.println(list);
        System.out.println(list.subList(0, 2));
    }
}
