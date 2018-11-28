package echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import java.net.InetSocketAddress;

/**
 * 服务端主程序
 * 1.用ServerBootstrap初始化
 * 2.用NioEventLoopGroup处理事件
 * 3.每个新连接的channel都使用同一个EchoServerHandler
 * 4.启动服务端channel
 */
public class EchoServer {
    private final int port=8888;
    public static void main(String[]args)throws Exception{
        new EchoServer().start();
    }
    public void start() throws Exception{
        final EchoServerHandler serverHandler=new EchoServerHandler();
        //NIO事件处理器的集合？（啥东西）
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            //启动server channel用的
            ServerBootstrap b=new ServerBootstrap();
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port));

            //把 编写的handle对象 放到pipeline最后节点上
            //它就可以处理入站消息啦（。。。）
            ChannelInitializer ci=new ChannelInitializer() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(serverHandler);
                }
            };
            b.childHandler(ci);

            //阻塞 直到服务器绑定channel完成
            ChannelFuture f=b.bind().sync();
            //阻塞 直到 channel关闭
            f.channel().closeFuture().sync();

        }finally {
            //优雅地关闭。。。
            group.shutdownGracefully().sync();
        }
    }
}
