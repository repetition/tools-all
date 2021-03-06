package com.tools.gui.process;


import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.LanguageFormatUtils;
import com.tools.constant.CommandMethodEnum;
import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GuiTest {


    @Test
    public void testMac() {

        String mac = GetMac();

        System.out.println(mac);

    }

    //获取本机的Mac地址
    public String GetMac()
    {
        InetAddress ia;
        byte[] mac = null;
        try {
            //获取本地IP对象
            ia = InetAddress.getLocalHost();
            //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //下面代码是把mac地址拼装成String
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<mac.length;i++){
            if(i!=0){
                sb.append("-");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length()==1?0+s:s);
        }

        //把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }

    @Test
    public void testMatcher() {

     //   String str = "<!-- keep 180 days' worth of history -->";
        /*读取文件*/
        String str = FileUtils.readFile("E:\\ThinkWin\\ThinkWinCR\\tomcat\\webapps\\ROOT\\WEB-INF\\classes\\logback.xml");
        //    log.info(str);
        //  webEngine.executeScript("setValue("+str+")");
        //codemirror \ 会被转义，所以赋值之前 直接给转义掉 将\ 替换成 \\
        String replace_Str = str.replace("\\", "\\\\");

        /*html不支持\r\n 需要 转义 \\r\\n */
        String replace = replace_Str.replace("\n", "\\n");
        //xml中如果存在 ' 号,在进行 executeScript时会报异常, setValue之前将所有'号替换成其他符号
        replace = replace.replace("'","&#x27;");

        System.out.println(replace);
    }


    @Test
    public void testPath() {

        CommandMethodEnum anEnum = CommandMethodEnum.getEnum(3002);
        CommandMethodEnum anEnum1 = CommandMethodEnum.getEnum(3002);
         LinkedList<CommandMethodEnum> commandMethodEnumList = new LinkedList<>();
        commandMethodEnumList.add(anEnum);
        commandMethodEnumList.add(anEnum1);
        Set<CommandMethodEnum> set = new HashSet<>();

        set.add(anEnum);
        set.add(anEnum1);

        for (CommandMethodEnum commandMethodEnum : commandMethodEnumList) {

            System.out.println(commandMethodEnum.hashCode());
        }

        LinkedHashSet<CommandMethodEnum> linkedHashSet = new LinkedHashSet<>();

        linkedHashSet.add(anEnum);
        linkedHashSet.add(anEnum1);


        linkedHashSet.remove(anEnum1);

        System.out.println(1);

    }

    @Test
    public void  testRep(){

        String str = " ServerAdmin webmaster@dummy-host.example.com\n" +
                "    JkMount /tk upload\n" +
                "    JkMount /upload upload\n" +
                "    JkMount /getuploadprogress upload\n" +
                "    JkMount /download upload\n" +
                "    JkMount /preview upload\n" +
                "    JkMount /deleteFile upload\n" +
                "    JkMount /deleteFiles upload\n" +
                "    JkMount /httpClientPost upload\n" +
                "    JkMount /api/uploadByBase64 upload\n" +
                "    JkMount /upload2 upload\n" +
                "\n" +
                "    JkMount /* cm\n" +
                "\n" +
                "    SetEnvIf REQUEST_URI \"${RESPATH}|\\.(dtd|rar|zip|json|js|css|xml|gif|png|jpeg|jpg|bmp|ico|pdf|swf|mp4|mp3|flv|avi|html|ttf|woff|woff2|eot|txt|svg)$\" no-jk\n" +
                "\n" +
                "    ServerName dummy-host.example.com\n" +
                "    ErrorLog \"logs/thinkwin-error.log\"\n" +
                "    ErrorDocument 404 http://10.10.11.127:82/jump404?baseSessionUserId=sys04\n" +
                "    CustomLog \"|bin/rotatelogs.exe ./logs/thinkwin-access_log-%Y_%m_%d.txt 86400 480\" common";

        Pattern pattern = Pattern.compile("\\|(.+?)rotatelogs.exe");

        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {

            System.out.println(matcher.group(0));


            str = str.replace(matcher.group(1)+"rotatelogs.exe","thinkwincr/bin/rotatelogs");

            System.out.println(str);

        }


    }



}
