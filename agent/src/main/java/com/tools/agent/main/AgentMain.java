package com.tools.agent.main;

import com.tools.agent.process.*;
import com.tools.socket.manager.SocketManager;
import com.tools.commons.thread.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentMain {
    private static final Logger log = LoggerFactory.getLogger(AgentMain.class);

    public static void main(String[] args) {
        //启动服务
        ThreadPoolManager.getInstance().execute(() -> {
            try {
                SocketManager.getSocketServer(6767).startServer();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("服务启动异常",e);
            }
        });
        //初始化处理器
/*        CMProcess cmProcess = new CMProcess();
        UploadProcess uploadProcess = new UploadProcess();
        ApacheProcess apacheProcess = new ApacheProcess();*/
        DeployProcess deployProcess = new DeployProcess();
        FileUploadProcess fileUploadProcess = new FileUploadProcess();
    }
}
