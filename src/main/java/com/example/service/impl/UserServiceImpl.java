package com.example.service.impl;

import com.example.pojo.User;
import com.example.service.UserService;
import com.springmvc.annotation.Service;

@Service
public class UserServiceImpl implements UserService{
    @Override
    public User findUser(String name) {
        if(name.equals("happy")) {
            return new User(1, "happy", 23, "123456");
        }
        return null;
    }

    @Override
    public String show(String name) {
        return name + "今天过情人节";
    }
}
