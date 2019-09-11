package com.tools.socket.observer;

import io.netty.channel.ChannelHandlerContext;

public interface Process {


    void process(Object obj, ChannelHandlerContext ctx);

    void active(ChannelHandlerContext ctx);

}
