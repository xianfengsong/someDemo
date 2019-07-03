package echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

/**
 * 客户端 i/o handler
 * 接收数据类型 设置为 ByteBuf
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 处理connect事件
     * 连接成功就发送数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes("AA".getBytes(Charset.forName("utf-8")));
        ctx.channel().writeAndFlush(buf);
        System.out.println("send msg");
    }

    /**
     * 处理服务端发来的数据
     * 数据在这会被释放掉
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg != null) {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("Client Receive:" + buf.toString(CharsetUtil.UTF_8));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
