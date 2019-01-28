package echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 服务端主程序
 * 1.用ServerBootstrap初始化
 * 2.用NioEventLoopGroup处理事件
 * 3.每个新连接的channel都使用同一个EchoServerHandler
 * 4.启动服务端channel
 */
public class EchoServer {

    public static void main(String[] args) throws Exception {
        new EchoServer().start();
    }

    private void start() {
        //负责accept连接的线程组
        NioEventLoopGroup parent = new NioEventLoopGroup();
        NioEventLoopGroup child = new NioEventLoopGroup();

        //启动server channel用的
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        int port = 8000;
        serverBootstrap.group(parent, child)
                .channel(NioServerSocketChannel.class)
                //ChannelInitializer泛型选择了NioServerSocketChannel导致没有数据？？？
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                });
        //服务器绑定
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("bind");
            } else {
                System.out.println("bind fail,check port");
            }
        });
//            //优雅地关闭。。。
//            group.shutdownGracefully().sync();
    }
}
