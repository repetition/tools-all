package com.tools.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestUtils {
    private static final Logger log = LoggerFactory.getLogger(RequestUtils.class);


    public static Proxy getProxy() {

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
        return proxy;
    }

    /**
     * 拼接参数
     * @param map  参数
     * @return
     */
    public static String formatParameters(Map<String,String> map){
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        StringBuilder builder = new StringBuilder();
        int flag = 0;
        for (Map.Entry<String, String> stringEntry : entrySet) {

            String key = stringEntry.getKey();
            String value = stringEntry.getValue();
            if (flag == 0) {
                builder.append(key+"="+value);
                flag++;
                continue;
            }
            builder.append("&"+key+"="+value);
        }
        log.info(builder.toString());
        return builder.toString();
    }

    public static String post(String url, String postStr) {

        HttpURLConnection connection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader buffer = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(1200);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            connection.setDoInput(true);
            if (null == postStr) {
                connection.setRequestMethod("GET");
            }
            if (postStr != null) {
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                OutputStream os = connection.getOutputStream();
                os.write(postStr.replace(",", "%2C").getBytes());
                os.flush();
                os.close();
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED || connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                isr = new InputStreamReader(is,Charset.forName("utf-8"));
                buffer = new BufferedReader(isr);
                String line;
                while ((line = buffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return "not_found";
            } else {
                return "fail";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }finally {
            IOUtils.close(is);
            IOUtils.close(isr);
            IOUtils.close(buffer);
        }
    }

    public static String post(String url, String postStr, String cookie) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader buffer = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(11200);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //必须设置false，否则会自动redirect到重定向后的地址
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Cookie", cookie);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
            if (null == postStr) {
                connection.setRequestMethod("GET");
            }
            if (postStr != null) {
                connection.setRequestMethod("POST");
                OutputStream osr = connection.getOutputStream();
                osr.write(postStr.getBytes(Charset.forName("utf-8")));
                osr.flush();
                osr.close();
            }
            log.info(connection.getResponseCode()+"");
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                isr = new InputStreamReader(is);
                buffer = new BufferedReader(isr);
                String line;
                while ((line = buffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                return HttpURLConnection.HTTP_FORBIDDEN+"";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            close(buffer);
            close(isr);
            close(is);
        }
        return "";
    }



    public static Map<String, Object> postAsMap(String url, String post) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader buffer = null;
        Map<String, Object> resMap = new HashMap<>();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(1200);
            //必须设置false，否则会自动redirect到重定向后的地址
            connection.setInstanceFollowRedirects(false);
            connection.setDoInput(true);
            if (null == post) {
                connection.setRequestMethod("GET");
            }
            if (post != null) {
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                OutputStream os = connection.getOutputStream();
                os.write(post.replace(",", "%2C").getBytes());
                //  os.write(post.getBytes());
                os.flush();
                os.close();
            }
            System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() == 200||connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                printHeader(connection);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP){
                    String location = connection.getHeaderField("Location");
                    resMap.put("location", location);
                }

                //获取cookie
                List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
                StringBuilder cookieStringBuilder = new StringBuilder();
                StringBuilder cookieJSessionIdStringBuilder = new StringBuilder();
                for (String cookie : cookies) {
                    if (cookie.contains("JSESSIONID")){
                        cookieJSessionIdStringBuilder.append(cookie);
                        cookieJSessionIdStringBuilder.append(";");
                        continue;
                    }
                    cookieStringBuilder.append(cookie);
                    cookieStringBuilder.append(";");
                }
                log.info(cookieStringBuilder.toString());
                log.info(cookieJSessionIdStringBuilder.toString());

                resMap.put("cookie", cookieStringBuilder.toString());
                resMap.put("JSESSIONID", cookieJSessionIdStringBuilder.toString());
                is = connection.getInputStream();
                isr = new InputStreamReader(is);
                buffer = new BufferedReader(isr);
                String line;
                while ((line = buffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                resMap.put("result", stringBuilder.toString());
            }
            return resMap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(buffer);
            close(isr);
            close(is);
        }
    }

    private static void printHeader(HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            log.info(entry.getKey() + " : " + entry.getValue());
        }
    }

    public static void close(Closeable io) {
        if (null != io) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
