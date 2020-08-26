package com.springmvc.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class TransactionManager {
    //A 线程 以A线程为key---->链接
    //B--->
    //只要是一个线程中取到就是同一个
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>(){
        @Override
        protected Connection initialValue() {
            return getConnection();
        }
    };
    public static Connection connection(){
        return threadLocal.get();
    }
    private  static Connection getConnection(){
        Connection root=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            root = DriverManager.getConnection("jdbc:mysql://127.0.0.1:12345/transaction?serverTimezone=UTC", "root", "123456");
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }
}

