package com.tools.service.service.deploy.impl;

import com.tools.commons.utils.PropertyUtils;
import com.tools.service.service.deploy.IConfigModifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Properties配置修改
 */
public class PropertiesModifyServiceImpl implements IConfigModifyService {
    private static final Logger log = LoggerFactory.getLogger(PropertiesModifyServiceImpl.class);

    @Override
    public void configModifying(String filePath, LinkedHashMap<String, String> linkedHashMap) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("cm配置文件 " + filePath + "不存在");
            return;
        }
        PropertyUtils propertyUtils = new PropertyUtils(file);
        propertyUtils.getConfiguration2Properties();
        linkedHashMap.entrySet().forEach(stringStringEntry -> {

            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();

            if (propertyUtils.getConfigurationPropertyStringByKey(key).equals("")) {
                log.warn(filePath + " Property key:" + key + "not found!");

            } else {
                log.info(filePath + " Property:" + key + " default value:" + propertyUtils.getConfigurationPropertyStringByKey(key) + " changed value:" + value);
                //修改配置文件
                propertyUtils.setConfigurationProperty(key, value);
            }
        });
    }
}
