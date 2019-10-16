package com.tools.service.service.deploy;

import com.tools.service.model.DeployStatusModel;

public interface ITomcatDeployService {

    /**
     * 清除tomcat缓存
     */
    void clearCacheForTomcat();
    /**
     * 清除tomcat旧文件
     */
    DeployStatusModel deleteOldFile();

    DeployStatusModel startTomcatForConsole();

    DeployStatusModel startTomcatForService();

    DeployStatusModel stopTomcatForConsole();

    DeployStatusModel stopTomcatForService();


    DeployStatusModel exportWarForTomcat();

    void configModifying();

    DeployStatusModel deleteWarFile();

}
