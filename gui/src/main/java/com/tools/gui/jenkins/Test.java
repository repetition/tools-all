package com.tools.gui.jenkins;


import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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


        Instant instant = new Date().toInstant();


        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(format);
        //转日日期
        LocalDate localDate = localDateTime.toLocalDate();

        System.out.println(localDate.toString());


    }
}
