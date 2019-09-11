package com.tools.gui.download;

import com.tools.gui.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadMain {
    public static final ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 30, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    private static final Logger log = LoggerFactory.getLogger(DownloadMain.class);
    public static int THREAD_COUNT = 5;
    private static String uri = "http://10.10.9.234/res/ROOT1.war";
    //private static String uri = "http://10.10.11.58:8080/job/中油瑞飞-thinkwin-cr/lastSuccessfulBuild/artifact/target/ROOT.war";

    private static String filePath = "G:\\ROOT.war";

    public static void main(String[] args) {

        int fileLength = 160356459;
      //  buildChunkInfos2(fileLength);
      //  buildChunkInfos3(fileLength);
    }

    /**
     * 下载的文件地址
     *
     * @param chunkInfos 分段下载区块
     */
    public static void downloadStart(List<ChunkInfo> chunkInfos) {


   /*     long totalSize = getFileTotalSize(uri);
        log.info("文件总大小：" + totalSize);
        createTempFile(totalSize);*/
        /**
         * 多线程下载
         */
        /*        List<ChunkInfo> chunkInfos = buildChunkInfos(totalSize);*/
        for (ChunkInfo chunkInfo : chunkInfos) {
            DownloadThread downloadThread = new DownloadThread(chunkInfo);
            executor.execute(downloadThread);
            //new Thread(downloadThread).start();
        }
    }

    public static List<ChunkInfo> buildChunkInfos3(int fileLength) {
        // 计算每条线程下载数据的大小
        //  int blockSize = fileLength / THREAD_COUNT;

        int blockSize = (fileLength + THREAD_COUNT - 1) / THREAD_COUNT;

        List<ChunkInfo> chunkInfos = new ArrayList<>();
        // 启动线程下载
        for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {/*
            // 核心代码，定义每个线程开始以及结束的下载位置
            int startPos = (threadId - 1) * blockSize;// 开始下载的位置
            int endPos = (threadId * blockSize) - 1;// 结束下载的位置（不包含最后一块）
            if (THREAD_COUNT == threadId) {
                endPos = fileLength - 1;
            }*/

            int start = threadId * blockSize;                     // 起始位置
            int end = threadId * blockSize + blockSize - 1;       // 结束位置

            if (threadId == (THREAD_COUNT - 1)) {
                end = end - 1;
            }

            //设置理论区块大小
            ChunkInfo chunkInfo = new ChunkInfo();
            chunkInfo.setBlockSize(blockSize);
            chunkInfo.setId(threadId);//设置id
            chunkInfo.setThreadCount(THREAD_COUNT);//设置id
            chunkInfo.setStartIndex(start);//设置起始位置
            chunkInfo.setEndIndex(end);//设置结束位置
            chunkInfo.setTotalSize(fileLength); //设置文件总大小
            chunkInfo.setUri(uri); //设置文件总大小
            chunkInfo.setFilePath(filePath);
            chunkInfos.add(chunkInfo);
            log.info("线程ID：" + threadId + "start:" + start + " endPos:" + end);
        }
        return chunkInfos;
    }

    public static List<ChunkInfo> buildChunkInfos2(int fileLength) {
        // 计算每条线程下载数据的大小
        int blockSize = fileLength / THREAD_COUNT;
        List<ChunkInfo> chunkInfos = new ArrayList<>();
        // 启动线程下载
        for (int threadId = 1; threadId <= THREAD_COUNT; threadId++) {
            // 核心代码，定义每个线程开始以及结束的下载位置
            int startPos = (threadId - 1) * blockSize;// 开始下载的位置
            int endPos = (threadId * blockSize) - 1;// 结束下载的位置（不包含最后一块）
            if (THREAD_COUNT == threadId) {
                endPos = fileLength - 1;
            }
            //设置理论区块大小
            ChunkInfo chunkInfo = new ChunkInfo();
            chunkInfo.setBlockSize(blockSize);
            chunkInfo.setId(threadId);//设置id
            chunkInfo.setThreadCount(THREAD_COUNT);//设置id
            chunkInfo.setStartIndex(startPos);//设置起始位置
            chunkInfo.setEndIndex(endPos);//设置结束位置
            chunkInfo.setTotalSize(fileLength); //设置文件总大小
            chunkInfo.setUri(uri); //设置文件总大小
            chunkInfo.setFilePath(filePath);
            chunkInfos.add(chunkInfo);
            log.info("线程ID：" + threadId + "start:" + startPos + " endPos:" + endPos);
        }
        return chunkInfos;
    }

    /**
     * 计算区块大小
     *
     * @param totalSize
     * @return
     */
    public static List<ChunkInfo> buildChunkInfos(long totalSize, String uri) {

        log.info(uri);
        log.info("name:" + getFileName(uri) + " | totalSize:" + totalSize);

        //计算每个线程的区块
        long blockSize = totalSize / THREAD_COUNT;//计算每个线程理论上下载的数量.
        List<ChunkInfo> chunkInfos = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            ChunkInfo chunkInfo = new ChunkInfo();
            long startIndex = i * blockSize; //线程开始下载的位置

            long endIndex = (i + 1) * blockSize - 1; //线程结束下载的位置
            if (i == (THREAD_COUNT - 1)) {  //如果是最后一个线程,将剩下的文件全部交给这个线程完成
                endIndex = totalSize - 1;
            }
            //设置理论区块大小
            chunkInfo.setBlockSize(blockSize);
            chunkInfo.setId(i);//设置id
            chunkInfo.setThreadCount(THREAD_COUNT);//设置id
            chunkInfo.setStartIndex(startIndex);//设置起始位置
            chunkInfo.setEndIndex(endIndex);//设置结束位置
            chunkInfo.setTotalSize(totalSize); //设置文件总大小
            chunkInfo.setUri(uri); //设置文件总大小
            chunkInfo.setFilePath(Config.downloadFilePath);
            chunkInfos.add(chunkInfo);
            log.info("线程" + i + "startIndex：" + startIndex + "| endIndex:" + endIndex);
        }
        return chunkInfos;
    }

    public static void createTempFile(long totalSize) {
        File file = new File(Config.downloadFilePath);
/*        if (file.exists()) {
            file.delete();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(totalSize);
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 后获取文件总大小
     *
     * @param uri
     * @return
     */
    public static long getFileTotalSize(String uri) {
        long fileSize = 0;
        try {
            URL url = new URL(uri);
            //  HttpURLConnection conn = (HttpURLConnection) url.openConnection(HTTPUtils.getProxy());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
                            + "application/x-shockwave-flash, application/xaml+xml, "
                            + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                            + "application/x-ms-application, application/vnd.ms-excel, "
                            + "application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            //   conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            log.info("http_code:" + conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Map<String, List<String>> headerFields = conn.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                    if (null == entry.getKey()) {
                        continue;
                    }
                    if (entry.getKey().contains("Content-Type")) {
                        log.info(entry.getKey() + " = " + entry.getValue());
                    }
                    if (entry.getKey().contains("Content-Length")) {
                        log.info(entry.getKey() + " = " + entry.getValue());
                    }
                    if (entry.getKey().contains("Status Code")) {
                        log.info(entry.getKey() + " = " + entry.getValue());
                    }
                }
                // 得到文件大小
                fileSize = conn.getContentLength();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSize;
    }


    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

}