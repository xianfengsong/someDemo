package zookeeper;

import java.util.UUID;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;

public class QueueTest {

    public static final String PATH="/test/queue";

    public static void test(){
        DistributedQueue<String> queue=null;
        CuratorFramework client=null;

        String instanceId=UUID.randomUUID().toString();

        try {
            client=SimpleClient.getClient();
            client.getCuratorListenable().addListener(new CuratorListener() {
                @Override
                public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                    System.out.println("event："+curatorEvent.getType().name());
                }
            });
            System.out.println(client.getZookeeperClient().getInstanceIndex());
            client.start();

            QueueConsumer<String>consumer=createQueueConsumer(instanceId);
            QueueBuilder<String> builder=QueueBuilder.builder(client,null,createQueueSerializer(),PATH);
            queue=builder.buildQueue();
            queue.start();

            for(int i=0;i<100;i++){
                queue.put("ID:"+instanceId+" msg:"+i);
                Thread.sleep(1000L);
            }
            while (true){
                Thread.sleep(20000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(queue);
            CloseableUtils.closeQuietly(client);
        }
    }

    /**
     * 队列消费者
     * @return
     */
    private static QueueConsumer<String> createQueueConsumer(final String id) {

        return new QueueConsumer<String>(){

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println("connection new state: " + newState.name());
            }

            @Override
            public void consumeMessage(String message) throws Exception {
                System.out.println(id+" \tconsume one message: " + message);
            }

        };
    }
    /**
     * 队列消息序列化类
     * @return
     */
    private static QueueSerializer<String> createQueueSerializer() {
        return new QueueSerializer<String>(){

            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };
    }

}
