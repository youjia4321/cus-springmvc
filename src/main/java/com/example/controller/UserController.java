package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.pojo.User;
import com.example.service.UserService;
import com.springmvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/get")
    public Map<String ,Object> getUser(HttpServletRequest request, @RequestParam("name") String name, String age) {
        Map<String, Object> map = new HashMap<>();
        System.err.println("年龄：" + age);
        User user = userService.findUser(name);
        if(user == null) {
            map.put("status", 404);
            map.put("user", null);
        } else {
            map.put("status", 200);
            map.put("user", JSON.toJSONString(user));
        }
        return map;
    }

}
