package io.niotest.reactor.basic;

import io.CommonConstants;
import io.niotest.reactor.handler.Handler;
import io.niotest.reactor.handler.IOHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * reactor模式 基础版
 * * 问题：
 * 1. 怎么判断这次读取了channel中的全部数据，特别是数据大小超过buffer大小
 * 3. handle处理都是同步的，并发会出现什么问题
 */
public class Reactor implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(CommonConstants.DEFAULT_PORT));
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey aSelected : selected) {
                    dispatch(aSelected);
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        Handler handler = (Handler) (key.attachment());
        if (handler != null) {
            //怎么是串行的？
            handler.run();
        }
    }

    //连接事件处理类
    class Acceptor implements Handler {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                System.out.println("server:接收新连接");
                if (channel != null) {
                    //为新的channel注册新事件
                    new IOHandler(selector, channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
