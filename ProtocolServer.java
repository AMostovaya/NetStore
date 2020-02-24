import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class ProtocolServer {
    private final int PORT = 7411;
    private ChannelFuture channelFuture = null;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void initialize() {
        CountDownLatch networkStarter = new CountDownLatch(1);
        new Thread(()-> Network.getInstance().start(networkStarter)).start();
        try {
            networkStarter.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void run() throws Exception {

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            channelFuture = b.bind(new InetSocketAddress(PORT)).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     public void stop() throws Exception{

        channelFuture.channel().closeFuture();
        channelFuture.awaitUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

    }
}
