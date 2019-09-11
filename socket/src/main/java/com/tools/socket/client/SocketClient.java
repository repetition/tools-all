package com.tools.socket.client;

import com.tools.commons.thread.ThreadPoolManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SocketClient {

    private SocketClientHandler clientHandler;
    private Channel channel;

    public String getHost() {
        return host;
    }

    public SocketClient setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public SocketClient setPort(int port) {
        this.port = port;
        return this;
    }

    private String host;
    private int port;

    public SocketClient() {

    }

    public boolean isConnect;

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean getConnect() {

        if (null == channel) {
            return false;
        }

        if (null != channel) {
            return channel.isOpen();
        }
        return false;
    }

    public Channel getChannel() {
        return channel;
    }

    public void connectServer() {
        if (channel != null && channel.isOpen()) {
            //判断当前连接是否进行切换ip连接
            if (!channel.remoteAddress().toString().contains(host)) {
                //关闭当前连接
                channel.close();
                //开启新连接
                connect();
            }else {
                onConnectedListener.onSuccess(channel);
            }
        } else {
            connect();
        }
    }

    /**
     * 执行连接
     */
    private void connect() {
        ThreadPoolManager.getInstance().execute(() -> {
            clientHandler = new SocketClientHandler();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                //添加编码器
                                // ch.pipeline().addLast("encoder",new LengthFieldPrepender(4,false));
                                // ch.pipeline().addLast(new LengthFieldPrepender(4,false));
                                ch.pipeline().addLast(new ObjectEncoder());
                                //添加解码器
                                // ch.pipeline().addLast("decoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                                // ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                                ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));

                                ch.pipeline().addLast(clientHandler);
                            }
                        });
                ChannelFuture channelFuture = bootstrap.connect(host, port);
                channelFuture.addListener(future -> {

                    if (future.isSuccess()) {
                        channel = channelFuture.channel();
                        onConnectedListener.onSuccess(channel);
                    } else {
                        onConnectedListener.onFail(new Exception("连接失败"));
                    }
                });

                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                onConnectedListener.onFail(e);
                isConnect = false;
            } finally {
                workerGroup.shutdownGracefully();
            }

        });
    }

    private OnConnectedListener onConnectedListener;

    public void setOnConnectedListener(OnConnectedListener onConnectedListener) {
        this.onConnectedListener = onConnectedListener;
    }

    public interface OnConnectedListener {
        void onSuccess(Channel channel);
       // void onState(String str);
        void onFail(Exception e);
    }

    public static void main(String[] args) {

        new SocketClient().setHost("127.0.0.1").setPort(6767).connectServer();
    }
}
