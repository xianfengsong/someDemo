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
 * Desc:
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

    @Override
    public void run() {
        try {
            state.handle(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
