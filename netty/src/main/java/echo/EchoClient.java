package echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端线程
 */
public class EchoClient {

    private static final int PORT = 8000;
    private static final String HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        new EchoClient().start();
    }

    private void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });
        //熟悉的connect() 异步的
        bootstrap.connect(HOST, PORT).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("connected");
            } else {
                System.out.println("connect fail");
            }
        });
    }

}
