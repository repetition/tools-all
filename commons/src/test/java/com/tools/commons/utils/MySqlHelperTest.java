package com.tools.commons.utils;

import com.mysql.jdbc.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MySqlHelperTest {

    @org.junit.Test
    public void getDBList() {

        String pinyin = LanguageFormatUtils.toHanyuPinyin("张三");

        System.out.println(pinyin);


    }

    @org.junit.Test
    public void getConnection() {


        Connection connection = MySqlHelper.getConnection("10.10.9.229", "root", "root");
        try {
            ResultSet resultSet = connection.prepareStatement("show databases").executeQuery();

            while (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    String string = resultSet.getString(i);

                    stringBuilder.append(string+ " , ");
                }
                System.out.println(stringBuilder.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
