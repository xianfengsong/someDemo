package io.remote.aio;

import io.CommonConstants;
import io.Utils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * AIO 客户端
 */
public class AIOClient implements Runnable {
    private AsynchronousSocketChannel asynchronousChannel;
    final static InetSocketAddress SERVER_ADDR = new InetSocketAddress(CommonConstants.DEFAULT_PORT);

    private static int clientNumber = 1;
    private final int id = clientNumber++;

    public AIOClient() {
        try {
            asynchronousChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        asynchronousChannel.connect(SERVER_ADDR, asynchronousChannel, new ConnectCompleteHandler());
    }

    class ConnectCompleteHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {
        /**
         * @param result     io操作得到的结果
         * @param attachment 对应channel指定的附件
         */
        public void completed(Void result, AsynchronousSocketChannel attachment) {
            try {
                System.out.println("(Connect)Handle by " + Thread.currentThread().getName());
                if (attachment.isOpen()) {
                    System.out.println("连接成功");
                    String msg = "im Client" + id;
                    //发送数据
                    ByteBuffer msgBuffer = ByteBuffer.wrap(msg.getBytes("utf8"));
                    //当前channel做附件
                    attachment.write(msgBuffer, 100, TimeUnit.MILLISECONDS, attachment, new WriteCompleteHandler());

                } else {
                    System.out.println("连接失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            exc.printStackTrace();
        }
    }

    class WriteCompleteHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        public void completed(Integer result, AsynchronousSocketChannel attachment) {
            System.out.println("(Write) Handle by " + Thread.currentThread().getName());
            if (result > 0) {
                System.out.println("send success");
                //读取服务端响应
                ByteBuffer respBuffer = ByteBuffer.allocate(CommonConstants.BUFFER_SIZE);
                attachment.read(respBuffer, 100, TimeUnit.MILLISECONDS, respBuffer, new ReadCompleteHandler());
            } else {
                System.out.println("send fail");
            }
        }

        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            exc.printStackTrace();
        }
    }

    class ReadCompleteHandler implements CompletionHandler<Integer, ByteBuffer> {
        public void completed(Integer result, ByteBuffer attachment) {
            try {
                System.out.println("(Read) Handle by " + Thread.currentThread().getName());

                //有数据
                if (result > 0) {
                    attachment.flip();
                    CharBuffer charBuffer = Utils.uft8Decoder.decode(attachment);
                    System.out.println(id+"recv:" + charBuffer.toString());
                } else {
                    System.out.println("read nothing");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void failed(Throwable exc, ByteBuffer attachment) {
            exc.printStackTrace();
        }
    }

}
