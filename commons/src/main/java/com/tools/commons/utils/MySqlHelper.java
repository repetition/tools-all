package com.tools.commons.utils;

import com.mysql.jdbc.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlHelper {

    private static final Logger log  = LoggerFactory.getLogger(MySqlHelper.class);

    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "root";
    private static Connection conn =null;


    public static List<String> getDBList(String dbAddress,String user,String pass){

        List<String> dbList = new ArrayList<>();
        Connection connection = getConnection(dbAddress,user,pass);
        if (null == connection) {
            dbList.add("数据库连接失败");
            log.error("数据库连接失败");
            return dbList;
        }
        //获取所有的数据库
        String sql = "show databases";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            dbList.clear();
            while (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String dbName = resultSet.getString(i);
                    dbList.add(dbName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if (null!=resultSet) {
                    resultSet.close();
                }
                if (null!=preparedStatement){
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("resultSet.close()",e);
            }
        }
        return dbList;
    }

    /**
     *
     * @param dbAddress
     * @param user
     * @param pass
     * @return
     */
    public static Connection getConnection(String dbAddress,String user,String pass) {
        // 注册 JDBC 驱动
        try {
            Class.forName(JDBC_DRIVER);
            // 打开链接
            log.info("连接数据库...");
            String DB_URL = "jdbc:mysql://"+dbAddress+":3306";
            String USER= user;
            String PASS = pass;
      /*      conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            String host = conn.getHost();*/

            if (null == conn){
                conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            }else if (!conn.getHost().equals(dbAddress)){
                conn.close();
                conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            }else if (conn.isClosed()){
                conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            }
            log.info("连接数据库成功");
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("运行异常",e);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("运行异常",e);
            return null;
        }
    }
}
