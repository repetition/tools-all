package com.tools.service.service.deploy.impl;

import com.tools.service.model.CommandModel;
import com.tools.service.service.deploy.IDeployProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class DeployProcessorServiceImpl extends ServiceBase implements IDeployProcessorService  {
    private static  final Logger log = LoggerFactory.getLogger(DeployProcessorServiceImpl.class);

    @Override
    public CommandModel deleteOldFiles(String filePath) {
        CommandModel commandModel = command.cmdDeleteFiles(filePath);

        return commandModel;

    }

    @Override
    public CommandModel deleteOldFile(String filePath) {
        CommandModel commandModel = command.cmdDeleteFile(filePath);


        return commandModel;
    }

    @Override
    public CommandModel exportWar(String warPath, String exportPath) {
        CommandModel commandModel = null;
        //判断文件是否存在
        File file = new File(warPath);
        if (!file.exists()) {
            commandModel = new CommandModel();
            commandModel.setProcessWaitFor(-1);
            commandModel.setProcessExcState(false);
            commandModel.setProcessOutputInfo(Arrays.asList(warPath+"不存在!"));
        }else {
            commandModel = command.exportZip(warPath,exportPath);
        }
        log.info("warPath:"+warPath);
        log.info(commandModel.toString());
        return commandModel;
    }
}
