package echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

/**
 * Server端 i/o handler
 * 处理入站事件（可读）的 handler
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 处理 read 事件
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("read");
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("Server receive:" + buf.toString(CharsetUtil.UTF_8));
        buf = ctx.alloc().buffer();
        buf.writeBytes("Server response: BBB".getBytes(Charset.forName("utf-8")));
        ctx.writeAndFlush(buf);
    }

    /**
     * 写入完成时事件处理（什么时候触发？）
     * @param ctx
     * @throws Exception
     */
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        //写入并调用flush（写空内容） 并添加对CLOSE事件的监听
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//                .addListener(ChannelFutureListener.CLOSE);
//    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
