package com.tools.agent.process;


import com.tools.commons.utils.GsonUtils;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApacheProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(ApacheProcess.class);
    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        log.info(GsonUtils.toJson(command));
    }
}
