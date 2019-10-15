package com.tools.service.service.deploy;

import com.tools.service.model.CommandModel;

import java.util.List;

public interface ILinuxInstallRpmService {

    CommandModel installRpm(String rpmPath);
    CommandModel checkRpm(List<String> command);

}
