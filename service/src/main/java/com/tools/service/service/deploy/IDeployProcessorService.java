package com.tools.service.service.deploy;

import com.tools.service.model.CommandModel;

public interface IDeployProcessorService {


    CommandModel deleteOldFiles(String filePath);
    CommandModel deleteOldFile(String filePath);

    CommandModel exportWar(String warPath, String exportPath);


}
