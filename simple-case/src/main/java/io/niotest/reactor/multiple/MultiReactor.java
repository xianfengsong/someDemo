package io.niotest.reactor.multiple;

import static io.CommonConstants.BUFFER_SIZE;

import io.CommonConstants;
import io.Utils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
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
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) it.next());
                }

                for (Selector subSelector : selectors) {
                    subSelector.select(100L);
                    Set<SelectionKey> subSet = subSelector.selectedKeys();
                    Iterator sit = subSet.iterator();

                    while (sit.hasNext()) {
                        dispatch((SelectionKey) sit.next());
                    }
                    subSet.clear();
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey key) {
        Runnable handler = (Runnable) (key.attachment());
        if (handler != null) {
            //怎么是串行的？
            handler.run();
        }
    }

    //连接事件处理类
    class Acceptor implements Runnable {

        @Override
        public synchronized void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();

                if (channel != null) {
                    System.out.println("server:accept connection");
                    //轮流选择selector
                    new Handler(selectors.get(next), channel);
                    if (++next == selectors.size() - 1) {
                        next = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class State {

        void handle(Handler handler) {
        }
    }

    class ReadState extends State {

        @Override
        void handle(Handler handler) {
            try {
                processRead(handler);
                //修改interest set
                handler.selectionKey.interestOps(SelectionKey.OP_WRITE);
                handler.selector.wakeup();
                handler.state = new SendState();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void processRead(Handler handler) throws IOException {
            ByteBuffer input = handler.input;
            ByteBuffer output = handler.output;

            String data = "";
            while (true) {
                //写入前先清空buffer
                input.clear();
                int count = handler.socketChannel.read(input);
                //-1表示channel传输结束
                if (count == -1) {
                    break;
                }
                //读取前翻转
                input.flip();
                CharBuffer charBuffer = Utils.uft8Decoder.decode(input);
                data += charBuffer.toString();
            }
            System.out.println("recv:" + data);

            //要回复的内容，提前写到output buffer
            String response = "echo:" + data;
            CharBuffer charBuffer = CharBuffer.wrap(response);
            output.put(Utils.uft8Encoder.encode(charBuffer));
        }
    }

    class SendState extends State {

        @Override
        void handle(Handler handler) {
            try {
                ByteBuffer output = handler.output;
                output.flip();
                //可能一次写不完
                while (output.hasRemaining()) {
                    handler.socketChannel.write(output);
                }
                output.clear();
                System.out.println("write response");
                //通道已经没有用，取消注册
                handler.selectionKey.cancel();
                //发送完成,断开写通道，让客户端知道传输结束
                handler.socketChannel.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读写事件处理类
    final class Handler implements Runnable {

        final SocketChannel socketChannel;
        final SelectionKey selectionKey;
        final Selector selector;
        ByteBuffer input = ByteBuffer.allocate(BUFFER_SIZE);
        ByteBuffer output = ByteBuffer.allocate(BUFFER_SIZE);
        State state;

        Handler(Selector sel, SocketChannel c) throws IOException {
            System.out.println(sel);
            socketChannel = c;
            selector = sel;
            socketChannel.configureBlocking(false);
            //初始化注册读事件
            selectionKey = socketChannel.register(sel, SelectionKey.OP_READ);
            //将Handler作为callback对象
            selectionKey.attach(this);
            //让阻塞的select方法立即返回？
            sel.wakeup();
            state = new ReadState();
        }

        @Override
        public synchronized void run() {
            try {
                state.handle(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
