package com.tools.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServer {
    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);
    private int port;

    private boolean isServerStart;

    public SocketServer() {

    }

    public SocketServer setPort(int port) {
        this.port = port;
        return this;
    }

    public void startServer() throws InterruptedException {
        if (!isServerStart) {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {

                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                //添加编码器
                                //   socketChannel.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                               // socketChannel.pipeline().addLast(new LengthFieldPrepender(4, false));
                                socketChannel.pipeline().addLast(new ObjectEncoder());

                                //添加解码器
                                //  socketChannel.pipeline().addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                               // socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                socketChannel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));

                                socketChannel.pipeline().addLast(new SocketServerHandler());
                            }
                        })
                        //注意以下是socket的标准参数
                        //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                        //Option是为了NioServerSocketChannel设置的，用来接收传入连接的
                        .option(ChannelOption.SO_BACKLOG, 128)
                        //心跳
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture channelFuture = bootstrap.bind(port);

                channelFuture.addListener(future -> {

                    if (future.isSuccess()) {
                        isServerStart = true;
                        log.info("服务启动成功! 端口:" + port);
                    } else {
                        isServerStart = false;
                    }
                });
                channelFuture.channel().closeFuture().sync();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }
}
