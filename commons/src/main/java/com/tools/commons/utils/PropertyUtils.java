package com.tools.commons.utils;

import com.tools.commons.thread.ThreadPoolManager;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Created by LIANGSE on 2018/4/26.
 */
public class PropertyUtils {

    private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);


    private  Properties mOrderedProperties = null;
    private  Properties mProperties = null;

    private File mPropertiesFile;
    private String propertiesName;
    private PropertiesConfiguration mPropertiesConfiguration;
    private PropertiesConfigurationLayout mLayout;
    ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder;
    PeriodicReloadingTrigger trigger;
    private Boolean isReload;

    private String configurationType;

    /**
     * 配置文件名字
     * @param propertiesName
     */
    public PropertyUtils(String propertiesName) {
        this.propertiesName = propertiesName;
        //初始化配置文件
        initPropertiesFile(propertiesName);
    }
    /**
     * 配置文件
     * @param propertiesFile 配置文件file
     */
    public PropertyUtils(File propertiesFile) {
        //初始化配置文件
        initProperties(propertiesFile);
    }

    /**
     * 获取顺序读取的配置文件读取实例
     *
     * @return
     */
    public Properties getOrderedProperties() {
       /* if (null == mOrderedProperties) {
            mOrderedProperties = new OrderedProperties();
        }*/
        //每次获取最新的配置文件
        mOrderedProperties = new OrderedProperties();
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(mPropertiesFile);
            inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("utf-8"));
            mOrderedProperties.load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(fileInputStream);
            IOUtils.close(inputStreamReader);
        }
        return mOrderedProperties;
    }
    /**
     * 获取Apache commons-configuration2 的Properties处理
     *
     * @return
     */
    public PropertiesConfiguration getConfiguration2ReloadProperties() {
        configurationType = "reload";
        //每次获取最新的配置文件
      //  mPropertiesConfiguration = new PropertiesConfiguration();
        mLayout = new PropertiesConfigurationLayout();

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(mPropertiesFile);
            //监测文件编码格式
            String encode = EncodeUtil.getEncode(mPropertiesFile.getAbsolutePath(), true);
         //   inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(encode));
            //加载配置文件
         //   mPropertiesConfiguration.setLayout(mLayout);
          //  mLayout.load(mPropertiesConfiguration,inputStreamReader);

            // Read data from this file
            Parameters parameters = new Parameters();
            builder = new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                    .configure(parameters.fileBased().setFile(mPropertiesFile).setEncoding(encode));

            trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),

                    null, 1, TimeUnit.SECONDS);
            trigger.start();
            mPropertiesConfiguration= builder.getConfiguration();

            Runnable run = () ->{
                    isReload = true;
                    while (isReload) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            mPropertiesConfiguration = builder.getConfiguration();
                        } catch (ConfigurationException e) {
                            e.printStackTrace();
                        }
                    }
            };
            ThreadPoolManager.getInstance().execute(run);
            // mPropertiesConfiguration.setLayout(mLayout);
            // mPropertiesConfiguration.getLayout().load(mPropertiesConfiguration,inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileInputStream);
            IOUtils.close(inputStreamReader);
        }
        return mPropertiesConfiguration;
    }


  /**
     * 获取Apache commons-configuration2 的Properties处理
     *
     * @return
     */
    public PropertiesConfiguration getConfiguration2Properties() {
        configurationType = "";

        //每次获取最新的配置文件
        mPropertiesConfiguration = new PropertiesConfiguration();
        mLayout = new PropertiesConfigurationLayout();

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(mPropertiesFile);
            //监测文件编码格式
            String encode = EncodeUtil.getEncode(mPropertiesFile.getAbsolutePath(), true);
               inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(encode));
            //加载配置文件
               mPropertiesConfiguration.setLayout(mLayout);
             mLayout.load(mPropertiesConfiguration,inputStreamReader);

            // mPropertiesConfiguration.setLayout(mLayout);
            // mPropertiesConfiguration.getLayout().load(mPropertiesConfiguration,inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileInputStream);
            IOUtils.close(inputStreamReader);
        }
        return mPropertiesConfiguration;
    }

    public void stopTrigger(){
        trigger.stop();
        isReload = false;
    }

    /**
     * 初始化配置文件File，需要传file对象
     * @param propertiesFile 配置文件 file对象
     */
    private void initProperties(File propertiesFile) {
        mPropertiesFile = propertiesFile;
    }

    /**
     * 获取一个系统默认的配置文件对象
     *
     * @return 配置文件对象
     */
    public  Properties getProperties() {
        mProperties= new Properties();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(mPropertiesFile);
            mProperties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(fileInputStream);
        }
        return mProperties;
    }

    public  File getPropertiesFile() {
        return mPropertiesFile;
    }

    /**
     * 初始化配置文件，文件不存在则创建配置文件,默认读取System.getProperty("conf.path") 路径
     * @param propertiesName 配置文件名称
     */
    private void initPropertiesFile(String filePath) {
        mPropertiesFile = null;
        mPropertiesFile = new File(filePath);
        if (!mPropertiesFile.exists()) {
            try {
                log.info(propertiesName + "配置文件不存在，创建配置文件,路径：" + mPropertiesFile.getAbsolutePath());
                mPropertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public  String getPropertyStringByKey(String key) {
        if (null == mProperties.getProperty(key)) {
            return "";
        }
        String value = mProperties.getProperty(key);
        return value;
    }

    public  Boolean getPropertyBooleanByKey(String key) {
        if (null == mProperties.getProperty(key)) {
            return false;
        }
        String property = mProperties.getProperty(key);
        return Boolean.valueOf(property);
    }

    public  void setProperty(String key, String value) {
        mProperties.setProperty(key, value);
        store(mProperties);
    }


    public  String getConfigurationPropertyStringByKey(String key) {
        if (null == mPropertiesConfiguration.getProperty(key)) {
            return "";
        }
        String value = mPropertiesConfiguration.getString(key);
        return value;
    }

    public  Boolean getConfigurationPropertyBooleanByKey(String key) {
        Boolean property = mPropertiesConfiguration.getBoolean(key,false);
        return property;
    }

    public  void setConfigurationProperty(String key, String value) {
      //  log.info(mPropertiesConfiguration.getLayout().hashCode()+" | "+mLayout.hashCode());
        mPropertiesConfiguration.setProperty(key, value);
        if (configurationType.equals("")){
            storePropertiesConfiguration(mPropertiesConfiguration);
        }else {
            try {
                builder.save();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public  void removeConfigurationPropertyByKey(String key) {
        mPropertiesConfiguration.clearProperty(key);
        storePropertiesConfiguration(mPropertiesConfiguration);
    }

    public  String getOrderedPropertyStringByKey(String key) {
        if (null == mOrderedProperties.getProperty(key)) {
            return "";
        }
        String property = mOrderedProperties.getProperty(key);
        return property;
    }

    public  Boolean getOrderedPropertyBooleanByKey(String key) {
        if (null == mOrderedProperties.getProperty(key)) {
            return false;
        }
        String property = mOrderedProperties.getProperty(key);
        return Boolean.valueOf(property);
    }

    public  void setOrderedProperty(String key, String value) {
        mOrderedProperties.setProperty(key, value);
        store(mOrderedProperties);
    }

    public  void removeOrderedPropertyByKey(String key) {
        mOrderedProperties.remove(key);
        store(mOrderedProperties);
    }

    public  void store(Properties properties) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mPropertiesFile);
            properties.store(fileOutputStream, "comment");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("配置文件保存异常：" + e);
        } finally {
            IOUtils.close(fileOutputStream);
        }
    }

    /**
     * 保存配置
     * @param configuration
     */
    private void storePropertiesConfiguration(PropertiesConfiguration configuration) {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream = new FileOutputStream(mPropertiesFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, Charset.forName("utf-8"));
            mLayout.save(mPropertiesConfiguration,outputStreamWriter);
          //  mPropertiesConfiguration.getLayout().save(mPropertiesConfiguration,outputStreamWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(fileOutputStream);
            IOUtils.close(outputStreamWriter);
        }
    }

    /**
     * 保存配置
     *
     * @param properties
     */
    public  void save(Properties properties) {
        store(properties);
    }
}
