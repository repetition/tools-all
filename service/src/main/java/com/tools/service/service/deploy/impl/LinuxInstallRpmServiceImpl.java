package com.tools.service.service.deploy.impl;

import com.tools.service.model.CommandModel;
import com.tools.service.service.command.ICommand;
import com.tools.service.service.command.factory.CommandFactory;
import com.tools.service.service.command.impl.LinuxCommandImpl;
import com.tools.service.service.deploy.ILinuxInstallRpmService;

import java.util.List;

public class LinuxInstallRpmServiceImpl implements ILinuxInstallRpmService {

    private final ICommand command;

    public LinuxInstallRpmServiceImpl() {
        command = CommandFactory.getCommand();

    }

    @Override
    public CommandModel installRpm(String rpmPath) {

        CommandModel commandModel = null;
        if (command instanceof LinuxCommandImpl) {
            commandModel = ((LinuxCommandImpl) command).cmdInstallRpm(rpmPath);
        }
        return commandModel;
    }

    @Override
    public CommandModel checkRpm(List<String> command) {

        CommandModel commandModel = null;
        if (this.command instanceof LinuxCommandImpl) {
            commandModel = ((LinuxCommandImpl) this.command).cmdCheckRpm(command);
        }
        return commandModel;
    }
}
