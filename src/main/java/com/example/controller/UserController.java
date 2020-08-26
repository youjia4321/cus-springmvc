package com.example.controller;
import com.example.pojo.User;
import com.example.service.TransactionalService;
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
    @Autowired
    TransactionalService transactionalService;

    @RequestMapping("/get")
    public String getUser(HttpServletRequest request, @RequestParam("name") String name, String age) {
        if(name != null) {
            User user = userService.findUser(name);
            request.setAttribute("user", user);
        }
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

    @ResponseBody
    @RequestMapping("/aop")
    public String aopTest(@RequestParam("name") String name) {
        return userService.show(name);
    }

    @ResponseBody
    @RequestMapping("/withdraw")
    public Map<String, Object> withdraw() {
        Map<String, Object> map = new HashMap<>();
        try {
            System.out.println("开启事务");
            transactionalService.withdraw();
            System.out.println("提交事务");
            map.put("status", 200);
            map.put("msg", "转账成功");
        } catch (Exception e) {
            map.put("status", 500);
            map.put("msg", "服务器异常");
            System.out.println("事务回滚");
        }
        return map;
    }

}
