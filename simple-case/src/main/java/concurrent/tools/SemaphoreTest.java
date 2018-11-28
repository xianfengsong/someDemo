package main.java.concurrent.tools;

import main.java.concurrent.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 信号量 控制资源池的同步访问
 * semaphore作用在 资源池为空时 阻塞调用者
 * 输出并没有展示出 公平模式和非公平的差距（测试方法不对）
 */
public class SemaphoreTest implements Test{
    Logger logger= LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private final int poolSize=5;
    boolean isFair=true;
    Semaphore semaphore;
    List<String> pool;
    public SemaphoreTest(boolean isFair){
        this.isFair=isFair;
        semaphore=new Semaphore(poolSize,isFair);
        pool=new ArrayList<String>();
        for(int i=0;i<poolSize;i++){
            pool.add("item "+i);
        }
    }

    public void test() throws BrokenBarrierException, InterruptedException {
        ExecutorService exec= Executors.newCachedThreadPool();
        for(int i=0;i<poolSize*2;i++){
            exec.execute(new PoolUser());
        }
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS);
    }
    class PoolUser implements Runnable{

        public void run() {
            long id=Thread.currentThread().getId();

            try {
                String item=borrow();
                if(item==null){
                    logger.info("borrow null");
                }else{
                    logger.info("{} - {}",id,item);
                    Thread.sleep(100L);
                    returnItem(item);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    String borrow() throws InterruptedException {
        Thread.yield();
        logger.info(Thread.currentThread().getId()+" call acquire");
        semaphore.acquire();
        synchronized (pool){
            for(int i=0;i<poolSize;i++){
                String s=pool.get(i);
                if(!s.contains("using")){
                    s+=" using";
                    pool.set(i,s);
                    return s;
                }
            }
        }
        return null;
    }
    void returnItem(String s){
        boolean returned=false;
        synchronized (pool){
            if(s!=null&&s.length()>0){
                for(int i=0;i<poolSize;i++){
                    String item=pool.get(i);
                    if(item.equals(s)){
                        item=item.replaceAll("using","");
                        pool.set(i,item);
                        returned=true;
                    }
                }
            }
        }
        if(returned){
            semaphore.release();
        }
    }


}
