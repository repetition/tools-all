package com.tools.agent.process;


import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserProcessTest {

    @Test
    public void processCommand() {

        FileBrowserProcess fileBrowserProcess = new FileBrowserProcess();

        fileBrowserProcess.processCommand(null, null);
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


    @Test
    public void listSort() {

        List<String> list = new ArrayList<>();
        list.add("c");
        list.add("o");
        list.add("b");
        list.add("k");
        list.add("z");
        list.add("p");
        list.add("p");

        list.sort((o1, o2) -> {

            return -o2.compareTo(o1);
        });

        System.out.println(list.size());



        String str = "djisoanafjoasdjiaosd";

        char[] chars = str.toCharArray();
        String copy = "";
        for (int i = chars.length - 1; i >= 0; i--) {
            copy +=chars[i];

        }

        System.out.println(copy);
        StringBuilder reverse = new StringBuilder(str).reverse();

        System.out.println(reverse);


        List<String> list1 = new ArrayList<>();

        list1.add("LISTEN1");
        list1.add("LISTEN2");
        list1.add("LISTEN3");
        list1.add("LISTEN4");
        list1.add("LISTEN5");
        list1.add("LISTEN");

        list1.removeIf(s -> {

            if (s.equals("LISTEN")) {
                return false;
            }
            return true;
        });


        System.out.println(list1.size());



    }


}
