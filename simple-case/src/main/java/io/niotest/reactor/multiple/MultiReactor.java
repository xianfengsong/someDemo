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
 * 交给线程池处理数据
 */
public class MultiReactor implements Runnable {

    private final List<Selector> selectors;
    //负责接收连接
    private final Selector selectorMain;
    private final ServerSocketChannel serverSocketChannel;
    private int next = 0;

    public MultiReactor(int port) throws IOException {
        selectors = new ArrayList<Selector>();
        selectors.add(Selector.open());
        selectors.add(Selector.open());
        selectors.add(Selector.open());
        selectorMain = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(CommonConstants.DEFAULT_PORT));
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel
                .register(selectorMain, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selectorMain.select();
                Set<SelectionKey> selected = selectorMain.selectedKeys();
                for (SelectionKey aSelected : selected) {
                    dispatch(aSelected);
                }

                for (Selector subSelector : selectors) {
                    subSelector.select(100L);
                    Set<SelectionKey> subSet = subSelector.selectedKeys();
                    for (SelectionKey aSubSet : subSet) {
                        dispatch(aSubSet);
                    }
                    subSet.clear();
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
        public synchronized void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();

                if (channel != null) {
                    System.out.println("server:accept connection");
                    //轮流选择selector
                    new IOHandler(selectors.get(next), channel);
                    if (++next == selectors.size() - 1) {
                        next = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
