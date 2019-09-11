package com.tools.service.service.deploy;

import java.util.LinkedHashMap;

/**
 * 配置文件修改
 */
public interface IConfigModifyService {
    /**
     * 配置修改
     */
    void configModifying(String filePath, LinkedHashMap<String, String> linkedHashMap);
}
