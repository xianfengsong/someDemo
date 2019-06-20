package io.niotest.clients;

import static io.CommonConstants.BUFFER_SIZE;
import static io.CommonConstants.DEFAULT_PORT;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.junit.Assert;

/**
 * author Xianfeng <br/>
 * date 19-6-18 上午11:24 <br/>
 * Desc:
 */
public class BlockingEchoClient implements Runnable {

    private SocketChannel socketChannel;
    private ByteBuffer buffer;
    private StringBuilder msg;

    /**
     * @param size 10的倍数，消息的长度
     */
    public BlockingEchoClient(int size) {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("localhost", DEFAULT_PORT));
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            msg = new StringBuilder(size);
            for (int i = 0; i < size / 10; i++) {
                msg.append("1234567890");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            buffer = ByteBuffer.wrap(msg.toString().getBytes());
            socketChannel.write(buffer);
            buffer.clear();
            socketChannel.read(buffer);
            String response = new String(buffer.array()).trim();
            Assert.assertEquals(msg.toString(), response);
            buffer.clear();
            socketChannel.close();
            System.out.println("exit!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
