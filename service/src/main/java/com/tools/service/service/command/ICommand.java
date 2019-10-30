package com.tools.service.service.command;

import com.tools.service.model.CommandModel;

public interface ICommand {


    CommandModel cmdSearchPid(String port);
    CommandModel cmdStartBat(String batPath );

    CommandModel cmdStartService(String serviceName);

    CommandModel cmdStopService(String serviceName);

    CommandModel cmdKillPid(String pidStr);

    CommandModel exportZip(String rootWarPath, String unRootWarPath);

    CommandModel cmdDeleteFiles(String filePath);

    CommandModel cmdQueryServiceStatus(String serviceName);


    CommandModel cmdCancelBootStartBySchtasks();

    CommandModel cmdSetBootStartBySchtasks();

    CommandModel cmdDeleteFile(String filePath);

    CommandModel cmdStopServerForCommand(String path);

    CommandModel cmdStartServerForCommand(String path);
}
