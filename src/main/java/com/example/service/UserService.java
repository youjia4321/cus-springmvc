package com.example.service;

import com.example.pojo.User;

public interface UserService {

    User findUser(String name);

    String show(String name);

}
