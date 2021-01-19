package concurrent.futuretest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    public Future<String> testNormalUsage() {
        CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> {
            Coder coder = new Coder(0);
            try {
                future.complete(coder.coding());
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
    public CompletableFuture<String> testFactoryMethod(int name) {
        return CompletableFuture.supplyAsync(() -> {
            //这里的代码会被commonPool中的线程执行
            try {
                Coder coder = new Coder(name);
                return coder.coding();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public Future<String> customThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Coder coder = new Coder(0);
                return coder.coding();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * 异步方法执行失败,get会抛出ExecutionException异常
     */
    @Test(expected = ExecutionException.class)
    public void testException() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("");
        });
        future.get();
    }

    /**
     * 使用join()等待所有任务完成,任务并发执行，用时小于串行时间
     * (这种方式使用基础的Future也能实现)
     */
    @Test
    public void waitAllBasic() {
        long start = System.currentTimeMillis();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CompletableFuture<String> future = testFactoryMethod(i);
            futures.add(future);
        }
        // 使用join，功能和get一样，但是会用unchecked exception代替checked异常,
        List<String> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        long time = System.currentTimeMillis() - start;
        System.out.println(results + " time=" + time);
        Assert.assertTrue(time < 1000 * 2);
    }

    /**
     * 使用allOf 等待future stream中的所有future返回结u个哦
     */
    @Test
    public void waitAllStream() {
        List<Coder> coders = new ArrayList<Coder>() {{
            add(new Coder(1));
            add(new Coder(2));
        }};
        //把参数流转成Future任务流并保存
        CompletableFuture[] futures = coders.stream().map(coder ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return coder.coding();
                    } catch (InterruptedException e) {
                        throw new RuntimeException();
                    }
                })).map(future -> future.thenApply(this::codeReview))
                .toArray(CompletableFuture[]::new);
        //等待所有任务返回
        CompletableFuture.allOf(futures).join();
    }

    /**
     * 测试使用apply转换future返回的结果
     * apply是在当前Future的执行线程内 同步调用的
     * （单独使用apply感觉没什么用，结果转换可以放到task内部啊）
     */
    @Test
    public void testApply() {
        long start = System.currentTimeMillis();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            //转换future结果
            CompletableFuture<String> future = testFactoryMethod(i).thenApply(this::codeReview);
            futures.add(future);
        }
        List<String> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        long time = System.currentTimeMillis() - start;
        System.out.println(results + " time=" + time);
        Assert.assertTrue(time < 1000 * 2);
    }

    /**
     * 使用compose将两个future任务组合到一个流水线中
     * A.thenCompose(B) A的结果会作为B的输入
     * thenCompose()的参数是能够返回completionStage的函数，而thenApply没有这个要求
     */
    @Test
    public void testCompose() {
        long start = System.currentTimeMillis();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        //执行code->codeReview
        for (int i = 0; i < 2; i++) {
            //和codeReview组合
            CompletableFuture<String> future = testFactoryMethod(i).thenCompose(
                    result -> CompletableFuture.supplyAsync(() -> this.codeReview(result)));
            futures.add(future);
        }
        List<String> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        long time = System.currentTimeMillis() - start;
        System.out.println(results + " time=" + time);
        //code之后才会执行codeReview，所以时间大于2秒
        Assert.assertTrue(time > 1000 * 2);
    }

    /**
     * 使用combine将几个平行的任务结果合并处理
     * 与compose不同，新任务不依赖上一个任务的结果
     */
    @Test
    public void testCombine() {
        long start = System.currentTimeMillis();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        //执行code1&code2->codeReview
        for (int i = 0; i < 2; i++) {
            CompletableFuture<String> future = testFactoryMethod(i).thenCombine(
                    //与新code任务组合
                    testFactoryMethod(i * 2),
                    //合并处理两个code任务的结果
                    (code1, code2) -> this.codeReview(code1 + code2));
            futures.add(future);
        }
        List<String> results = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        long time = System.currentTimeMillis() - start;
        System.out.println(results + " time=" + time);
        //被combine的任务是并行执行的，虽然执行两次code和一次review,但时间还是小于3秒
        Assert.assertTrue(time < 1000 * 3);
    }


    /**
     * 执行 code review 处理code
     */
    private String codeReview(String code) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("oh, very good,coder=" + code + ",codeReview thread=" + Thread.currentThread().getId());
        return "reviewed:" + code;
    }

    static class Coder {

        private int name;

        public Coder(int name) {
            this.name = name;
        }

        public String coding() throws InterruptedException {
            try {
                Thread.sleep(1000L);
                return name + " done,thread=" + Thread.currentThread().getId();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }
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
