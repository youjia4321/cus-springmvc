package com.example.service.impl;

import com.example.dao.UserDao;
import com.example.service.TransactionalService;
import com.springmvc.annotation.Autowired;
import com.springmvc.annotation.Service;
import com.springmvc.annotation.Transactional;

@Service
@Transactional
public class TransactionalServiceImpl implements TransactionalService {
    // 通过反射后的对象不能自动注入(在代码中做了处理)
    // @Autowired
    UserDao userDao = new UserDao();

    @Override
    public void withdraw() {
        userDao.insert("lisi", 500);
        userDao.insert("zhangsan", -500);
//        int i= 1/0;
    }
}
