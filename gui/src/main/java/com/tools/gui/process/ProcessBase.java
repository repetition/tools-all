package com.tools.gui.process;

import com.tools.commons.utils.GsonUtils;
import com.tools.service.model.DeployState;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import com.tools.socket.observer.ObserverManager;
import com.tools.socket.observer.Process;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ProcessBase implements Process {
    private static final Logger log = LoggerFactory.getLogger(ProcessBase.class);
    private ChannelHandlerContext ctx;

    ProcessBase() {
        log.info(this.getClass().getName() + " init!");
        ObserverManager.getSocketClientObserverable().registerProcess(this);
    }

    @Override
    public void process(Object obj, ChannelHandlerContext ctx) {
        if (obj instanceof Command) {
            processCommand((Command)obj, ctx);
        }
        if (obj instanceof FileUpload) {
            processFileUpload((FileUpload) obj, ctx);
        }
    }

    @Override
    public void active(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        if (onDeployProcessorListener != null) {
            //连接成功调用init
            onDeployProcessorListener.onDeployInit();
        }
    }

    /**
     * 通过socket发送消息
     * @param obj 消息
     */
    public void sendMessage(Object obj) {
        if (ctx==null) {
            error();
        } else if (ctx.channel().isOpen()){
            send(obj);
        }
    }

    private void send(Object obj) {
        if (obj instanceof Command) {
            ctx.channel().writeAndFlush((Command)obj);
        }
        if (obj instanceof FileUpload) {
            ctx.channel().writeAndFlush(obj);
        }
    }

    protected void error() {
        log.error("当前没有可用的tcp连接!");
    }

    /**
     * socket command命令回调 (子类实现,父类不做实现)
     * @param command 命令
     * @param ctx 通道
     */
    protected void processCommand(Command command, ChannelHandlerContext ctx) {
    }
    /**
     * socket 文件上传命令回调 (子类实现,父类不做实现)
     * @param fileUpload 文件实体
     * @param ctx 通道
     */
    protected void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {
    }

    public OnDeployProcessorListener onDeployProcessorListener;

    public void setOnDeployProcessorListener(OnDeployProcessorListener onDeployProcessorListener) {
        this.onDeployProcessorListener = onDeployProcessorListener;
    }

    public interface OnDeployProcessorListener {
        /**
         * 部署开始之前,进行初始化操作
         */
        void onDeployInit();

        /**
         * 部署开始
         */
        void onDeployProcessorStart();

        /**
         * 部署结束
         */
        void onDeployProcessorEnd();

        /**
         * 分阶段部署成功
         *
         * @param deployState 部署的详细信息
         */
        void onDeployProcessSuccess(DeployState deployState);


        /**
         * 部署失败
         *
         * @param deployState 失败的详细信息
         */
        void onDeployProcessFail(DeployState deployState);

    }
}
