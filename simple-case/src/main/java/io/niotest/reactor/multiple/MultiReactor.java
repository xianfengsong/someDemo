package io.niotest.reactor.multiple;

import io.CommonConstants;
import io.niotest.reactor.handler.Handler;
import io.niotest.reactor.handler.IOHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 有多个Selector, mainSelector负责处理连接事件，执行accept,subSelector负责IO事件
 */
public class MultiReactor implements Runnable {

    private final List<Selector> selectors;
    //负责接收连接
    private final Selector selectorMain;
    private final ServerSocketChannel serverSocketChannel;
    private int next = 0;

    public MultiReactor() throws IOException {
        selectors = new ArrayList<>();
        selectors.add(Selector.open());
        selectors.add(Selector.open());
        selectors.add(Selector.open());
        selectorMain = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(CommonConstants.DEFAULT_PORT));
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selectorMain, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selectorMain.select();
                Set<SelectionKey> selected = selectorMain.selectedKeys();
                //发现connect事件，调用Acceptor
                for (SelectionKey key : selected) {
                    dispatch(key);
                }
                selected.clear();
            }
            //todo subSelector应该同时工作吧
            for (Selector subSelector : selectors) {
                Thread subSelectorThread = new Thread(() -> {
                    while (!Thread.interrupted()) {
                        try {
                            subSelector.select();
                            System.out.println(subSelector);
                            Set<SelectionKey> subSet = subSelector.selectedKeys();
                            for (SelectionKey aSubSet : subSet) {
                                dispatch(aSubSet);
                            }
                            subSet.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                subSelectorThread.start();
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
        public synchronized void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();

                if (channel != null) {
                    //轮流选择selector,把新的客户端连接交付
                    new IOHandler(selectors.get(next % (selectors.size() - 1)), channel);
                    next++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
