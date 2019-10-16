package com.tools.service.service.deploy;

import com.tools.service.model.DeployStatusModel;

public interface IApacheDeployService {

    DeployStatusModel stopService();

    DeployStatusModel startService();


    void changeHttpdConf();

    void changeWorkers();


    DeployStatusModel exportWar();

    DeployStatusModel deleteFiles();
    DeployStatusModel deleteWarFile();

}
