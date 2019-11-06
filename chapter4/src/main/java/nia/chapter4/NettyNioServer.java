package nia.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Listing 4.4 Asynchronous networking with Netty
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class NettyNioServer {

    public static void main(String[] args) throws Exception {
        new NettyNioServer().server(8080);
    }


    public void server(int port) throws Exception {
        final ByteBuf buf =
                Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n",
                        Charset.forName("UTF-8")));
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            final EchoServerHandler eh = new EchoServerHandler();
            ServerBootstrap b = new ServerBootstrap();
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch)
                                              throws Exception {
                                          ch.pipeline().addLast(eh);
                                          ch.pipeline().addLast(
                                                  new ChannelInboundHandlerAdapter() {
                                                      @Override
                                                      public void channelActive(
                                                              ChannelHandlerContext ctx) throws Exception {
                                                          ctx.writeAndFlush(buf.duplicate());
                                                      }
                                                  });
                                      }
                                  }
                    );
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}

