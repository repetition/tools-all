package com.tools.agent.main;

import com.tools.agent.process.*;
import com.tools.socket.manager.SocketManager;
import com.tools.commons.thread.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

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

        DeployProcess deployProcess = new DeployProcess();
        FileUploadProcess fileUploadProcess = new FileUploadProcess();
        SyncConfigProcess cmProcess = new SyncConfigProcess();
        FileBrowserProcess fileBrowserProcess = new FileBrowserProcess();
        ServiceControlProcess serviceControlProcess = new ServiceControlProcess();
        String rootPath = System.getProperty("user.dir");

        System.setProperty("dir.base", rootPath);
        //解压工具路径
        System.setProperty("HaoZip.path", System.getProperty("dir.base") + "/HaoZipC");
        //配置文件路径
        System.setProperty("conf.path", System.getProperty("dir.base") + "/conf/");

        //HaoZip.path
        System.err.println("HaoZip.path: "+System.getProperty("HaoZip.path"));
        //程序工作目录
        System.err.println("dir.base: "+System.getProperty("dir.base"));

        System.err.println("conf.path: "+System.getProperty("conf.path"));


        Properties properties = System.getProperties();

        for (String stringPropertyName : properties.stringPropertyNames()) {

            System.out.println( stringPropertyName + ": "+System.getProperty(stringPropertyName));

        }
    }
}
