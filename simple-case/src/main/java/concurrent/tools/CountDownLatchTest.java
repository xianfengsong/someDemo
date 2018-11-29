package concurrent.tools;

import static concurrent.tools.CountDownLatchTest.Judge.gameEndLatch;
import static concurrent.tools.CountDownLatchTest.Judge.gameStartLatch;

import concurrent.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch测试
 * Runner
 * 通过await()等待gameStartLatch为零，比赛开始
 * 通过调用gameEndLatch countDown()方法表示完成比赛
 *
 * Judge
 * 调用gameStartLatch countDown()方法表示比赛开始
 * 通过await(timeout)等待gameEndLatch为零，比赛结束
 */
public class CountDownLatchTest implements Test{

    private final static int RUNNER_NUMBER=10;

    public void test() {
        ExecutorService exec= Executors.newFixedThreadPool(RUNNER_NUMBER);
        Runner runner=new Runner();
        for(int i=0;i<RUNNER_NUMBER;i++){
            exec.execute(runner);
        }
        Judge.handleGame();
        exec.shutdown();
    }

    class Runner implements Runnable{

        public void run() {
            try {
                Long id= Thread.currentThread().getId();
                gameStartLatch.await();
                Thread.sleep(ThreadLocalRandom.current().nextLong(500,1000));
                System.out.println(id + "到达终点！");
                gameEndLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static class Judge {
        static CountDownLatch gameStartLatch=new CountDownLatch(1);
        static CountDownLatch gameEndLatch=new CountDownLatch(RUNNER_NUMBER);
        static void handleGame(){
            try {
                Thread.sleep(1000L);
                System.out.println("宣布比赛开始");
                gameStartLatch.countDown();

                gameEndLatch.await(2000L, TimeUnit.MILLISECONDS);
                if(gameEndLatch.getCount()==0L){
                    System.out.println("宣布比赛结束");
                }else {
                    System.out.println("比赛超时！");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
