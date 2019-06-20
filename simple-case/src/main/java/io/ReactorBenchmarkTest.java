package io;

import static io.CommonConstants.CLIENT_MODE;
import static io.CommonConstants.SERVER_MODE;

import io.niotest.aio.AIOClient;
import io.niotest.aio.AIOServer;
import io.niotest.clients.BlockingEchoClient;
import io.niotest.reactor.multiple.MultiReactor;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Before;
import org.junit.Test;

/**
 * nio reactor模式 性能测试
 *
 * @author xianfeng
 */
public class ReactorBenchmarkTest {

    private int messageLength;

    /**
     * aio模式
     */
    private static void aio(String[] args) {
        try {
            String arg = args[0];
            if (SERVER_MODE.equals(arg)) {
                Thread server = new Thread(new AIOServer());
                server.start();
            } else if (CLIENT_MODE.equals(arg)) {
                ExecutorService executor = Executors.newFixedThreadPool(20);
                for (int i = 0; i < 20; i++) {
                    executor.execute(new AIOClient());
                }
                executor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void startServer() {
        try {
            Thread server = new Thread(new MultiReactor());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void run() {
        BlockingEchoClient client = new BlockingEchoClient(messageLength);
        Thread thread = new Thread(client);
        thread.start();
    }

}
