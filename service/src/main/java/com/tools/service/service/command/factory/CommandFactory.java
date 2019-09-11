package com.tools.service.service.command.factory;

import com.tools.service.service.command.ICommand;
import com.tools.service.service.command.impl.LinuxCommandImpl;
import com.tools.service.service.command.impl.WindowsCommandImpl;

public class CommandFactory {

    public static ICommand getCommand() {

        ICommand command = null;
        String osName = System.getProperty("os.name");
        if (osName.contains("windows")) {
            command = new WindowsCommandImpl();
        }

        if (osName.contains("linux")) {
            command = new LinuxCommandImpl();
        }
        return command;
    }
}
