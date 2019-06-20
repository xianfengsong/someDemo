package io.niotest.reactor.handler;

import static io.CommonConstants.BUFFER_SIZE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * author Xianfeng <br/>
 * date 19-1-15 下午8:22 <br/>
 * Desc: IO处理类，有一个状态机State,初始状态是Read
 * IOHandler作为selectionKey的附件，subSelector在监听到IO事件时，会调用
 */
public class IOHandler implements Handler {

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    final Selector selector;
    ByteBuffer input = ByteBuffer.allocate(BUFFER_SIZE);
    ByteBuffer output = ByteBuffer.allocate(BUFFER_SIZE);
    State state;


    public IOHandler(Selector sel, SocketChannel c) throws IOException {
        socketChannel = c;
        selector = sel;
        c.configureBlocking(false);
        //初始化注册读事件
        selectionKey = socketChannel.register(sel, SelectionKey.OP_READ);
        //将Handler作为callback对象
        selectionKey.attach(this);
        //让阻塞的select方法立即返回？
        sel.wakeup();
        state = new ReadState();
    }

    /**
     * 因为把IOHandler作为附件注册到了selectionKey，所以run()方法会在IO事件发生时被subSelector调用
     */
    @Override
    public void run() {
        try {
            state.handle(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
