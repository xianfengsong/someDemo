package io.niotest.reactor.handler;

import io.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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
        ByteBuffer input = ioHandler.input;
        ByteBuffer output = ioHandler.output;

        String data = "";
        while (true) {
            //写入前先清空buffer
            input.clear();
            int count = ioHandler.socketChannel.read(input);
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
