package com.springmvc.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandlerMapping {

    private String uri;
    private Object controller;
    private Method method;
    private String[] args;

    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 获取参数
        Object[] parameters = new Object[args.length];
        for(int i = 0; i < args.length; i++) {
            parameters[i] = req.getParameter(args[i]);
        }
        System.out.println(Arrays.toString(parameters));
        // 反射调用当前控制器方法
        Object response = method.invoke(controller, parameters);
        resp.getWriter().println(response.toString());
    }

}
