package com.tools.commons.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    private static DecimalFormat df;

    //四舍五入把double转化为int类型整数,0.5也舍去,0.51进一
    public static int DoubleFormatInt(Double dou) {
        //四色五入转换成整数
        if (null ==df) {
            df = new DecimalFormat("######0");
        }
        return Integer.parseInt(df.format(dou));
    }

    /**
     * 获取屏幕高度
     * @return 返回屏幕高度
     */
    public static double getScreenHeight(){
        double height = Screen.getPrimary().getBounds().getHeight();
        return height;
    }

    /**
     * 获取屏幕宽度
     * @return 返回屏幕宽度
     */
    public static double getScreenWidth(){
        double width = Screen.getPrimary().getBounds().getWidth();
        return width;
    }

    /**
     * 获取本机ip
     * @return 返回获取本机ip
     */
    public static String getLocalIP(){
        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return IP != null ? IP.getHostAddress() : null;
    }

    /**
     * 解压文件
     *
     * @param rootWarPath   ROOT.war路径
     * @param unRootWarPath ROOT.war解压路径路径
     */
    public static boolean unZIP(String rootWarPath, String unRootWarPath) {
        FileOutputStream decFOS =null;
        try {
            long start = System.currentTimeMillis();
            ZipFile zipFile = new ZipFile(rootWarPath, Charset.forName("GBK"));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            int size = zipFile.size();
            log.info("文件数：" + size);
            while (entries.hasMoreElements()) {
                //获取每个文件实体
                ZipEntry zipEntry = entries.nextElement();
                //获取文件名字
                String zipEntryName = zipEntry.getName();
                long millis = zipEntry.getLastModifiedTime().toMillis();
                String string = zipEntry.getLastModifiedTime().toString();
                //  log.info(zipEntryName + " - " + string);
                //文件夹不存在则创建
                File decDir = new File(unRootWarPath);
                if (!decDir.exists()) {
                    decDir.mkdirs();
                }
                File outFile = new File(decDir.getAbsoluteFile() + "\\" + zipEntry.getName());
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }
                //解压文件如果是文件夹则创建文件夹
                if (zipEntry.isDirectory()) {
                    outFile.mkdirs();
                    //设置最后修改时间
                    boolean setLastModified = outFile.setLastModified(millis);
                    //  log.info("修改：" + setLastModified);
                    //如果是文件夹则创建文件夹跳出循环
                    continue;
                }
                //解压文件如果是文件则创建文件写入
                InputStream zipIS = zipFile.getInputStream(zipEntry);
                //  log.info(outFile.getAbsolutePath() + "  - " + outFile.toString());
                outFile.createNewFile();
                decFOS = new FileOutputStream(outFile);
                byte[] bytes = new byte[1024 * 8];
                int len;
                while ((len = zipIS.read(bytes)) != -1) {
                    decFOS.write(bytes, 0, len);
                }
                decFOS.flush();
                decFOS.close();
                boolean setLastModified = outFile.setLastModified(millis);
                zipIS.close();
                // log.info(zipEntryName);
            }
            zipFile.close();
            long end = System.currentTimeMillis();
            log.info("解压完毕 - 耗时:" + (end - start) / 1000 + "s");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("解压异常！", e);
            //  showAlert("错误", "解压错误", e.getMessage());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Exception Dialog");
                    alert.setHeaderText("Look, an Exception Dialog");
                    alert.setContentText("UnZipFail!");

                    TextArea textArea = new TextArea();
                    textArea.setWrapText(true);
                    textArea.setEditable(false);
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    textArea.setMaxWidth(Double.MAX_VALUE);

                    StringWriter stringWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stringWriter));
                    textArea.setText(stringWriter.toString());
                    alert.getDialogPane().setExpandableContent(textArea);
                    alert.show();
                }
            });
            return false;
        }finally {
            if (null != decFOS) {
                try {
                    decFOS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归删除文件
     *
     * @param unRootWarPath
     */
    public static boolean deleteAllFile(String unRootWarPath) {
        try {
            File ROOT_file = new File(unRootWarPath);
            if (!ROOT_file.exists()) {
                return true;
            }
            File[] listFiles = ROOT_file.listFiles();
            for (File file : listFiles) {

                if (file.isDirectory()) {
                    deleteAllFile(file.getAbsolutePath());
                    //空文件夹直接删除
                    file.delete();
                    // log.info(file.getAbsolutePath() + "文件夹删除成功!");
                }
                //如果是文件直接删除
                if (file.isFile()) {
                    file.delete();
                    // log.info(file.getAbsolutePath() + "文件删除成功!");
                }
            }
            //删除root目录
            if (ROOT_file.listFiles().length == 0) {
                ROOT_file.delete();
                //  log.info(ROOT_file.getAbsolutePath() + "文件删除成功!");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("运行异常：", e);
            return false;
        }
        return true;
    }

    /**
     * * 获取MD5加密
     * *
     * * @param pwd
     * *            需要加密的字符串
     * * @return String字符串 加密后的字符串
     */
    public synchronized static String getMD5(String str) {
        try {
            // 创建加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");

            // 调用加密对象的方法，加密的动作已经完成
            byte[] bs = digest.digest(str.getBytes());
            // 接下来，我们要对加密后的结果，进行优化，按照mysql的优化思路走
            // mysql的优化思路：
            // 第一步，将数据全部转换成正数：
            String hexString = "";
            for (byte b : bs) {
                // 第一步，将数据全部转换成正数：
                // 解释：为什么采用b&255
                /*
                 * b:它本来是一个byte类型的数据(1个字节) 255：是一个int类型的数据(4个字节)
                 * byte类型的数据与int类型的数据进行运算，会自动类型提升为int类型 eg: b: 1001 1100(原始数据)
                 * 运算时： b: 0000 0000 0000 0000 0000 0000 1001 1100 255: 0000
                 * 0000 0000 0000 0000 0000 1111 1111 结果：0000 0000 0000 0000
                 * 0000 0000 1001 1100 此时的temp是一个int类型的整数
                 */
                int temp = b & 255;
                // 第二步，将所有的数据转换成16进制的形式
                // 注意：转换的时候注意if正数>=0&&<16，那么如果使用Integer.toHexString()，可能会造成缺少位数
                // 因此，需要对temp进行判断
                if (temp < 16 && temp >= 0) {
                    // 手动补上一个“0”
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String getIPForAddress(SocketAddress address){

        String ip = address.toString();
        //截取符合规则的ip地址
        ip = ip.split(":")[0].split("/")[1];

        return ip;
    }

    /**
     * 获取所有匹配内容
     *
     * @param regex  匹配正则
     * @param source 匹配的字符串
     * @return List方式返回所有匹配的字符串 null没有匹配
     */
    public static List<String> matcherAll(String regex, String source) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        List<String> matchers = new ArrayList<>();
        if (!matcher.find()) {
            return matchers;
        }
        for (int i = 0; i <= matcher.groupCount(); i++) {
            matchers.add(matcher.group(i));
        }
        return matchers;
    }

    public static String getUUID32() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
//  return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
