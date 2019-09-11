package com.tools.socket.client;

import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import com.tools.socket.observer.ObserverManager;
import com.tools.commons.thread.ThreadPoolManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SocketClientHandler.class);
    private ChannelHandlerContext ctx;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)   {
        log.info("channelRegistered" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)   {
        log.info("channelUnregistered" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)   {
        log.info("channelActive" + ctx.channel().remoteAddress());
        this.ctx = ctx;
        ObserverManager.getSocketClientObserverable().active(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        log.info("channelRead" + ctx.channel().remoteAddress());
        ThreadPoolManager.getInstance().execute(() -> {

            if (msg instanceof Command) {
                ObserverManager.getSocketClientObserverable().receive(msg, ctx);
            }

            if (msg instanceof FileUpload) {
                ObserverManager.getSocketClientObserverable().receive((FileUpload) msg, ctx);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
    }


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void sendMsg(String msg) {

        if (null != ctx) {
            byte[] bytes = msg.getBytes();
            ByteBuf byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            ctx.writeAndFlush(byteBuf);

            if (msg.equals("q")) {
                ctx.close();
            }
        }
    }

}
