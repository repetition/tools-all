package com.tools.service.service.deploy.impl;

import com.tools.service.constant.ServiceStateEnum;
import com.tools.service.model.CommandModel;
import com.tools.service.service.deploy.IServerControlService;

import java.util.Arrays;

public class ServerControlServiceImpl extends ServiceBase implements IServerControlService {

    @Override
    public CommandModel startServerForCommand(String batPath) {

        CommandModel commandModel = command.cmdStartBat(batPath);

        return commandModel;

    }

    @Override
    public CommandModel startServerForService(String serviceName) {

        CommandModel commandModel = command.cmdStartService(serviceName);
        Object processExcState = commandModel.getProcessExcState();

        switch ((ServiceStateEnum) processExcState) {
            case STATED:
                commandModel.setProcessExcState(true);
                commandModel.setProcessOutputInfo(Arrays.asList("服务启动成功!"));
                break;
            case ERROR:
                commandModel.setProcessExcState(false);
            case NOT_EXIST:
                commandModel.setProcessExcState(false);
        }
        return commandModel;

    }

    @Override
    public CommandModel stopServerForCommand(String port) {
        CommandModel stopServerCommandModel = new CommandModel();

        CommandModel commandModel = findProcessPidByPort(port);
        Object excState = commandModel.getProcessExcState();

        if (excState instanceof String && !((String) excState).isEmpty()) {
            stopServerCommandModel = command.cmdKillPid((String) excState);
        } else {
            stopServerCommandModel.setProcessExcState(true);
            stopServerCommandModel.setProcessOutputInfo(Arrays.asList("服务没有启动"));
        }
        return stopServerCommandModel;
    }

    @Override
    public CommandModel stopServerForService(String serviceName) {
        CommandModel stopServiceCommandModel = new CommandModel();

        CommandModel commandModel = command.cmdQueryServiceStatus(serviceName);
        Object excState = commandModel.getProcessExcState();
        if (excState instanceof ServiceStateEnum) {

            switch ((ServiceStateEnum) excState) {
                case STATED:
                    stopServiceCommandModel = stopService(serviceName, commandModel);
                    break;
                case STOPPED:
                    stopServiceCommandModel.setProcessExcState(true);
                    stopServiceCommandModel.setProcessEexcTime(commandModel.getProcessExecTime());
                    stopServiceCommandModel.setProcessOutputInfo(Arrays.asList("服务没有启动"));
                    break;
                case NOT_EXIST:
                    stopServiceCommandModel.setProcessExcState(false);
                    stopServiceCommandModel.setProcessEexcTime(commandModel.getProcessExecTime());
                    stopServiceCommandModel.setProcessOutputInfo(Arrays.asList("服务不存在"));
                    break;
                case STOP_PENDING:
                    stopServiceCommandModel.setProcessExcState(false);
                    stopServiceCommandModel.setProcessEexcTime(commandModel.getProcessExecTime());
                    stopServiceCommandModel.setProcessOutputInfo(Arrays.asList("服务正在停止中"));
                    break;
                case START_PENDING:
                    stopServiceCommandModel.setProcessExcState(false);
                    stopServiceCommandModel.setProcessEexcTime(commandModel.getProcessExecTime());
                    stopServiceCommandModel.setProcessOutputInfo(Arrays.asList("服务正在启动中"));
                    break;
                case ERROR:
                    stopServiceCommandModel.setProcessExcState(false);
                    stopServiceCommandModel.setProcessEexcTime(commandModel.getProcessExecTime());
                    stopServiceCommandModel.setProcessOutputInfo(Arrays.asList("服务正在启动中"));
                    break;
            }
        }
        return stopServiceCommandModel;
    }

    private CommandModel stopService(String serviceName, CommandModel commandModel) {
        CommandModel stopServiceCommandModel = new CommandModel();
        CommandModel serviceCommandModel = command.cmdStopService(serviceName);

        Object excState = serviceCommandModel.getProcessExcState();
        if (excState instanceof ServiceStateEnum) {
            stopServiceCommandModel.setProcessEexcTime(commandModel.getProcessExecTime());
            stopServiceCommandModel.setProcessOutputInfo(commandModel.getProcessOutputInfo());
            switch ((ServiceStateEnum) excState) {
                case ERROR:
                    stopServiceCommandModel.setProcessExcState(false);
                    break;
                case STOPPED:
                    stopServiceCommandModel.setProcessExcState(true);
                    break;
            }
        }
        return stopServiceCommandModel;
    }

    @Override
    public CommandModel findProcessPidByPort(String port) {

        CommandModel commandModel = command.cmdSearchPid(port);

        return commandModel;
    }

    @Override
    public CommandModel findServiceStatus(String serviceName) {

        CommandModel commandModel = command.cmdQueryServiceStatus(serviceName);
        return commandModel;
    }
}
