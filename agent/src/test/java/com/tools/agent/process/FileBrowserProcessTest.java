package com.tools.agent.process;


import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserProcessTest {

    @Test
    public void processCommand() {

        FileBrowserProcess fileBrowserProcess = new FileBrowserProcess();

        fileBrowserProcess.processCommand(null,null);
    }

    @Test
    public void suffixFilter() {

        List<String> filter = new ArrayList<>();
        filter.add(".properties");
        filter.add(".xml");

        File file = new File("F:\\Tools\\CR.properties");

        //获取文件名
        String fileName = file.getName();

        int lastIndexOf = fileName.lastIndexOf(".");

        String substring = fileName.substring(lastIndexOf, fileName.length());
        System.out.println(substring);
    }



}
