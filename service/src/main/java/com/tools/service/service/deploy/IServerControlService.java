package com.tools.service.service.deploy;

import com.tools.service.model.CommandModel;

public interface IServerControlService {
    /**
     * 以控制台启动服务
     * @param path  路径
     */
    CommandModel startServerForCommand(String path);
    /**
     * 以windows服务形式 启动服务
     * @param serviceName  服务名称
     */
    CommandModel startServerForService(String serviceName);

    /**
     * 以命令行形式停止服务
     * @param path
     */
    CommandModel stopServerForCommand(String path);
    /**
     * 以windows服务形式 停止服务
     * @param serviceName  服务名称
     */
    CommandModel stopServerForService(String serviceName);

    /**
     * 根据端口查找进程PID
     * @param port
     */
    CommandModel findProcessPidByPort(String port);

    /**
     * 查找windows 服务的 运行状态
     * @param path
     */
    CommandModel findServiceStatus(String path);

    CommandModel stopServerForLinux(String path);

    CommandModel startServerForLinux(String path);
}
