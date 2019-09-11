package com.tools.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;


public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    public static File mFile;

    private static WatchService watchService;
    private static boolean isRun;

    public static FileOutputStream getFileOutputStream(String fileName) {
  /*      if (isDebug) {
            mFile = new File(debugConfigPath + "/JenkinsProperties.properties");
        } else {
            mFile = new File(System.getProperty("dir.base") + "/conf/JenkinsProperties.properties");
        }*/
        mFile = new File(System.getProperty("conf.path") + fileName);

        if (!mFile.exists()) {
            try {
                log.info("配置文件不存在，创建配置文件");
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mFile);
            return fileOutputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取文件
     * @param filePathStr 文件路径
     * @return
     */
    public static String readFile(String filePathStr) {
        FileInputStream fileInputStream = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        StringBuilder builder = null;
        try {
            fileInputStream = new FileInputStream(filePathStr);
            //监测文件编码格式
            String encode = EncodeUtil.getEncode(filePathStr, true);
            // reader = new InputStreamReader(fileInputStream, Charset.forName("utf-8"));
            reader = new InputStreamReader(fileInputStream,Charset.forName(encode));
            bufferedReader = new BufferedReader(reader);
            String line = "";
            String result = "";
            builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileInputStream);
            IOUtils.close(reader);
            IOUtils.close(bufferedReader);
        }

        return builder.toString();
    }

    /**
     * 文件保存
     * @param sourceStr 保存的内容
     * @param filePath  保存的路径
     */
    public static void saveFile(String sourceStr,String filePath) {

        String editorText = sourceStr;
        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(editorText.getBytes(Charset.forName("utf-8")));
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(fileOutputStream);
        }
    }

    /**
     * 监测文件变化
     * @param filePath 文件路径
     */
    public static void initFileChangedMonitor(String  filePath ,boolean[] isChanged){
        isRun = true;
        File file = new File(filePath);
        // 需要监听的文件目录（只能监听目录）
        String path = file.getParentFile().getAbsolutePath();

        watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path p = Paths.get(path);
            p.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_CREATE);
            WatchService finalWatchService = watchService;
            Thread thread = new Thread(() -> {
                try {
                    while(isRun){
                        WatchKey watchKey = finalWatchService.take();
                        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                        for(WatchEvent<?> event : watchEvents){
                            log.info("["+path+"/"+event.context()+"]文件发生了["+event.kind()+"]事件");
                            //TODO 根据事件类型采取不同的操作。。。。。。。
                            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                if (event.context().toString().equals(file.getName())) {
                                    log.info("["+path+"/"+event.context()+"]文件发生了["+event.kind()+"]事件");
                                    isChanged[0] = true;
                                }
                            }
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                if (event.context().toString().equals(file.getName())) {
                                    log.info("[" + path + "/" + event.context() + "]文件发生了[" + event.kind() + "]事件");
                                    isChanged[0] = true;
                                }
                            }
                        }
                        watchKey.reset();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.setDaemon(false);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void closeFileChangedMonitor(){
        try {
            isRun = false;
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
