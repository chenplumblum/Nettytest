package A0Netty基础.OIO;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @Auther: cpb
 * @Date: 2018/9/26 11:51
 * @Description:
 */
public class NettyOioServer {

    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
//        阻塞队列
        EventLoopGroup group = new OioEventLoopGroup();
        try {
//         创建ServerBootstrap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
//            使用 NioEventLoopGroup 允许非阻塞模式（NIO）
            serverBootstrap.group(group)
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {            //4
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);//5
                        }
                    });
                }
            });
            ChannelFuture f = serverBootstrap.bind().sync();  //6
            f.channel().closeFuture().sync();
        }finally{
            group.shutdownGracefully().sync();
        }
    }
}
