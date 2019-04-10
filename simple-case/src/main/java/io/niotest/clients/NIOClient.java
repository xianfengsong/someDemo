package io.niotest.clients;

import static io.CommonConstants.BUFFER_SIZE;

import io.CommonConstants;
import io.Utils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 通用的客户端
 */
public class NIOClient implements Runnable {

    private final static InetSocketAddress SERVER_ADDR = new InetSocketAddress(
            CommonConstants.DEFAULT_PORT);
    private Selector selector;
    private SocketChannel channel;

    public NIOClient() throws IOException {
        selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);

        SelectionKey key = channel.register(selector, SelectionKey.OP_CONNECT);
//        key.interestOps(SelectionKey.OP_READ);
        channel.connect(SERVER_ADDR);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) it.next());
                    it.remove();
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) throws IOException {
        if (key.isConnectable()) {

            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }
            String msg = "hello, i am " + Thread.currentThread().getName();
            ByteBuffer sendBuffer = Utils.uft8Encoder.encode(CharBuffer.wrap(msg));
            int count = channel.write(sendBuffer);
            if (count == msg.length()) {
                channel.shutdownOutput();
                System.out.println("send:" + msg);
            } else {
                System.out.println("send fail");
            }
            channel.register(selector, SelectionKey.OP_READ);
            selector.wakeup();
        } else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key
                    .channel();
            ByteBuffer input = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                //写入前先清空buffer
                input.clear();
                int count = channel.read(input);
                //-1表示channel传输结束
                if (count == -1) {
                    break;
                }
                //读取前翻转
                input.flip();
                if (input.limit() != 0) {
                    CharBuffer charBuffer = Utils.uft8Decoder.decode(input);
                    System.out.println("recv:" + charBuffer);
                }
            }
        }

    }
}


