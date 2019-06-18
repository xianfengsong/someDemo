package io.niotest.reactor.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * author Xianfeng <br/>
 * date 19-1-15 下午8:21 <br/>
 * Desc:
 */
public class ReadState implements State {

    @Override
    public void handle(IOHandler ioHandler) {
        try {
            processRead(ioHandler);
            //修改interest set
            ioHandler.selectionKey.interestOps(SelectionKey.OP_WRITE);
            ioHandler.selector.wakeup();
            ioHandler.state = new SendState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRead(IOHandler ioHandler) throws IOException {
        ByteBuffer inputBuf = ioHandler.input;
        //写入前先清空buffer
        inputBuf.clear();
        ioHandler.socketChannel.read(inputBuf);
        String msg = new String(inputBuf.array()).trim();
        ioHandler.output = ByteBuffer.wrap(msg.getBytes());
    }
}
