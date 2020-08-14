package com.example.controller;
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
    public String getUser(HttpServletRequest request, @RequestParam("name") String name) {
        User user = userService.findUser(name);
        System.out.println(user);
        return "user";
    }

    @ResponseBody
    @RequestMapping("/map")
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "success");
        map.put("status", 200);
        map.put("result", "你想得美！");
        return map;
    }

    @ResponseBody
    @RequestMapping("/string")
    public String getString() {
        return "200";
    }

}
