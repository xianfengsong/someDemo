package io.niotest.classic;

import io.niotest.clients.BlockingEchoClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 20-1-13 上午10:18 <br/>
 * Desc: 测试调整serverSocket的backLog对tcp listen overflow的影响
 */
public class ListenOverflowTest {

    @Before
    public void startServer() {
        Thread t = new Thread(new Server(128));
        t.start();
    }

    /**
     * 修改backLog的值，运行test,使用watch -d 'netstat -s|grep -i listen' 观察变化
     * backLog=50 : 287 -> 335
     * backLog=128 : 335 -> 347
     */
    @Test
    public void startClient() throws InterruptedException {
        int number = 100;
        int messageLen = 1000;
        ExecutorService exec = Executors.newFixedThreadPool(number);
        for (int i = 0; i < number * 10; i++) {
            exec.execute(new BlockingEchoClient(messageLen));
        }
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.HOURS);
    }
}
