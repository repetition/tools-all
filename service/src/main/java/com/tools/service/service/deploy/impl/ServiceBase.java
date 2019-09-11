package com.tools.service.service.deploy.impl;

import com.tools.service.service.command.ICommand;
import com.tools.service.service.command.factory.CommandFactory;

public class ServiceBase {

    public ICommand command;

    public ServiceBase() {
        command = CommandFactory.getCommand();
    }
}
