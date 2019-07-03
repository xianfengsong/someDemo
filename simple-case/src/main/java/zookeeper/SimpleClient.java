package zookeeper;


import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * curator客户端测试
 */
public class SimpleClient {

    private static final String REAL_SERVER = "192.168.185.153:2181,192.168.185.154:2181,10.252.112.209:2181";

    private static CuratorFramework client = null;

    static {
//        TestingServer server = new TestingServer(2181);
        client = CuratorFrameworkFactory.builder()
                .connectString(REAL_SERVER)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    }

    public static CuratorFramework getClient() {
        return client;
    }

    private static void watchTest() {
        try {
            TestingServer server = new TestingServer(2181);
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(server.getConnectString())
                    .sessionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

            client.start();
            System.out.println(client.getState());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void main(String[] args) {
        try {
//            QueueTest.test();

            client.start();
            System.out.println(client.getChildren().forPath(QueueTest.PATH).toString());
            List<String> children = client.getChildren().forPath(QueueTest.PATH);
            for (String s : children) {
                System.out.println(s);
            }
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
