package io.niotest.reactor.handler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * author Xianfeng <br/>
 * date 19-1-15 下午8:24 <br/>
 * Desc:
 */
public class SendState implements State {

    @Override
    public void handle(IOHandler ioHandler) {
        try {
            ByteBuffer output = ioHandler.output;
            output.flip();
            //可能一次写不完
            while (output.hasRemaining()) {
                ioHandler.socketChannel.write(output);
            }
            output.clear();
            System.out.println("write response");
            //通道已经没有用，取消注册
            ioHandler.selectionKey.cancel();
            //发送完成,断开写通道，让客户端知道传输结束
            ioHandler.socketChannel.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
