package io.niotest.aio;

import io.CommonConstants;
import io.Utils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * aio 服务端
 */
public class AIOServer implements Runnable {
    private AsynchronousServerSocketChannel asynchronousChannel;

    public AIOServer(int port) {
        try {
            //线程池的线程用来执行各种IO事件对应的handler
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
            asynchronousChannel = AsynchronousServerSocketChannel.open(group);
            asynchronousChannel.bind(new InetSocketAddress(CommonConstants.DEFAULT_PORT));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            asynchronousChannel.accept(asynchronousChannel, new AcceptCompleteHandler());
            while (!Thread.interrupted()){
                //do something
                Thread.sleep(1000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class AcceptCompleteHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
        /**
         * @param result     io操作得到的结果
         * @param attachment 对应channel指定的附件
         */
        public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
            System.out.println("(Accept)Handle by " + Thread.currentThread().getName());
            //server socket去接收下一个连接
            attachment.accept(attachment, new AcceptCompleteHandler());

            //读取新连接的channel中的数据
            ByteBuffer msgBuffer = ByteBuffer.allocate(CommonConstants.BUFFER_SIZE);
            //socket channel做附件给handler,把读到内容原路返回
            result.read(msgBuffer, 100, TimeUnit.MILLISECONDS, result, new ReadCompleteHandler(msgBuffer));
        }

        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
            exc.printStackTrace();
        }
    }

    class ReadCompleteHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        ByteBuffer msg;

        public ReadCompleteHandler(ByteBuffer msg) {
            this.msg = msg;
        }

        public void completed(Integer result, AsynchronousSocketChannel attachment) {
            try {
                System.out.println("(Read) Handle by " + Thread.currentThread().getName());

                //有数据
                if (result > 0) {
                    msg.flip();
                    CharBuffer charBuffer = Utils.uft8Decoder.decode(msg);
                    System.out.println(charBuffer.toString());
                    String resp = "echo:" + charBuffer.toString();
                    ByteBuffer respBuffer = ByteBuffer.wrap(resp.getBytes("utf8"));
                    attachment.write(respBuffer, 100, TimeUnit.MILLISECONDS, null, new WriteCompleteHandler());
                } else {
                    System.out.println("read nothing");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            exc.printStackTrace();
        }
    }

    class WriteCompleteHandler implements CompletionHandler<Integer, Void> {

        public void completed(Integer result, Void attachment) {
            System.out.println("(Write) Handle by " + Thread.currentThread().getName());

            if (result > 0) {
                System.out.println("send success");
            } else {
                System.out.println("send fail");
            }
        }

        public void failed(Throwable exc, Void attachment) {
            exc.printStackTrace();
        }
    }
}
