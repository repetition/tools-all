package com.tools.gui.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class DownloadThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DownloadThread.class);
    private static long downloadFileSize = 0;
    private ChunkInfo chunkInfo;

    private static int download_count;

    private long thread_download_count;


    public static final int DOWNLOAD_SUCCESS = 0;
    public static final int DOWNLOAD_FAIL = 1;
    public static final int DOWNLOAD_DOWNLOADING = 2;
    public static final int DOWNLOAD_DOWN = 3;

    public DownloadThread(ChunkInfo chunkInfo) {
        this.chunkInfo = chunkInfo;

    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        log.info("线程：" + chunkInfo.getId() + "正在下载....");
        try {
            URL url = new URL(chunkInfo.getUri());
          //  HttpURLConnection connection = (HttpURLConnection) url.openConnection(HTTPUtils.getProxy());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
            connection.setRequestProperty("Connection", "Keep-Alive");
            //设置分段下载的头信息。  Range:做分段数据请求用的。格式: Range bytes=0-1024  或者 bytes:0-1024
            connection.setRequestProperty("Range", "bytes=" + chunkInfo.getStartIndex() + "-" + chunkInfo.getEndIndex());
            connection.setReadTimeout(10000);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                log.info("HTTP_Request_Error: code" + connection.getResponseCode());
                mOnDownloadListener.onDownloadFail(DOWNLOAD_FAIL, chunkInfo.getFilePath());
                return;
            }
/*            Map<String, List<String>> headerFields = connection.getHeaderFields();
            log.info("----------------------" + chunkInfo.getId() + "---------------------");
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                if (null == entry.getKey()) {
                    continue;
                }
                if (entry.getKey().contains("Server")) {
                    log.info(entry.getKey() + " = " + entry.getValue());
                }
                if (entry.getKey().contains("Content-Range")) {
                    log.info(entry.getKey() + " = " + entry.getValue());
                }
                if (entry.getKey().contains("Content-Length")) {
                    log.info(entry.getKey() + " = " + entry.getValue());
                }
                if (entry.getKey().contains("Status Code")) {
                    log.info(entry.getKey() + " = " + entry.getValue());
                }
                if (entry.getKey().contains("Accept-Ranges")) {
                    log.info(entry.getKey() + " = " + entry.getValue());
                }
            }*/
            File file = new File(chunkInfo.getFilePath());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            //  randomAccessFile.setLength(chunkInfo.getTotalSize());
            randomAccessFile.seek(chunkInfo.getStartIndex());
            InputStream is = connection.getInputStream();
            byte[] b = new byte[1024 * 2];
            int len = -1;
            long length = 0;
            //  mOnDownloadListener.onDownloadProgress(DOWNLOAD_DOWN, downloadFileSize, 0);
            while ((len = is.read(b)) != -1) {
                randomAccessFile.write(b, 0, len);
                length += len;
                //计算总进度
                fileSizeSum(len);
                //计算每个线程进度
                thread_download_count += len;
            }
            is.close();
            randomAccessFile.close();
            log.info("thread_ID:" + chunkInfo.getId() + " | threadDownload:" + thread_download_count + "length:" + length);
        } catch (IOException e) {
            e.printStackTrace();
            mOnDownloadListener.onDownloadFail(DOWNLOAD_FAIL, chunkInfo.getFilePath());
        }
/*        download_count++;
        if (chunkInfo.getThreadCount() == download_count) {
            long end = System.currentTimeMillis();
            log.info("downloadSuccess | time:" + (end - start) / 1000 + "s");
            log.info("thread:" + chunkInfo.getId() + "|" + chunkInfo.getTotalSize() + "|downloadFileSize:" + downloadFileSize);
            download_count = 0;
        }*/
        log.info("downloadFileSize:" + downloadFileSize +" - chunkInfo.getTotalSize():"+chunkInfo.getTotalSize());
        if (downloadFileSize == chunkInfo.getTotalSize()) {
            long end = System.currentTimeMillis();
            log.info("downloadSuccess | time:" + (end - start) / 1000 + "s");
            log.info("thread:" + chunkInfo.getId() + "|" + chunkInfo.getTotalSize() + "|downloadFileSize:" + downloadFileSize);
            mOnDownloadListener.onDownloadSuccess(DOWNLOAD_SUCCESS, chunkInfo.getFilePath());
            downloadFileSize = 0;
        }else {
            //mOnDownloadListener.onDownloadFail(DOWNLOAD_FAIL, chunkInfo.getFilePath());
        }
    }

    /**
     * 计算文件总的下载进度
     *
     * @param len
     */
    public void fileSizeSum(int len) {
        synchronized (DownloadThread.class) {
            downloadFileSize += len;
            DecimalFormat format = new DecimalFormat("0.00");

            double prgresss = Double.valueOf(downloadFileSize) / Double.valueOf(chunkInfo.getTotalSize());

//            log.info("下载进度：" + format.format(prgresss) + "%");
            mOnDownloadListener.onDownloadProgress(DOWNLOAD_DOWNLOADING, downloadFileSize, chunkInfo.getTotalSize());
        }
    }

    private static OnDownloadListener mOnDownloadListener;

    public static void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
    }


    public interface OnDownloadListener {

        void onDownloadSuccess(int state, String path);

        void onDownloadFail(int state, String path);

        void onDownloadProgress(int state, long downloadFileSize, long progress);

    }
}
