package com.example.dao;

import com.springmvc.annotation.Component;
import com.springmvc.utils.TransactionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Component
public class UserDao {

    public  void insert(String username, int money){
        Connection root =null;
        try {
            root = TransactionManager.connection();
            PreparedStatement preparedStatement = root.prepareStatement("update users set money=money-" + money + " where username='" + username+"'");
            int i = preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UserDao().insert("zhangsan",500);
    }

}
