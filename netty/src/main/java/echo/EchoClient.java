package echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 客户端线程
 *
 */
public class EchoClient {
    private final String host;
    private final int port=8888;
    public EchoClient(String host){
        this.host=host;
    }
    public static void main(String [] args)throws Exception{
        String host=args[0];
        new EchoClient(host).start();
    }
    public void start()throws Exception{
        EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
        try{
            Bootstrap b=new Bootstrap();

            b.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            //这怎么不用单例的handler了？
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //熟悉的connect() 阻塞的
            ChannelFuture f=b.connect().sync();
            f.channel().closeFuture().sync();
        }finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

}
