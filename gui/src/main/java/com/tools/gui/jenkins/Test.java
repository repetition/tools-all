package com.tools.gui.jenkins;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {

        List<String> stringList = new ArrayList<>();

        stringList.add("111111");
        stringList.add("222");

        List<String> collect = stringList.stream().filter(s -> s.equals("222")).collect(Collectors.toList());
        System.out.println("22223324");

    }
}
