package com.springmvc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmvc.model.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class HandlerExecutionChain {

    public HandlerMapping handler;
    private Method method;
    private Object bean;

    public HandlerExecutionChain(HandlerMapping handler) {
        this.handler = handler;
        this.method = handler.getMethod();
        this.bean = handler.getController();
    }

    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp)
            throws InvocationTargetException, IllegalAccessException, IOException {
        // 获取参数
        Object[] parameters = new Object[handler.getArgs().length];
        // System.out.println("当前控制器上的参数列表：" + Arrays.toString(handler.getArgs()));
        for(int i = 0; i < handler.getArgs().length; i++) {
            if("HttpServletRequest".equals(handler.getArgs()[i])){
                parameters[i] = req;
            } else if("HttpServletResponse".equals(handler.getArgs()[i])) {
                parameters[i] = resp;
            } else {
                parameters[i] = req.getParameter(handler.getArgs()[i]);
            }
        }
        Object response = method.invoke(bean, parameters);
        if(handler.isResponseBody()) {
            resp.setContentType("application/json;charset=utf-8");
            ObjectMapper objectMapper = new ObjectMapper();
            resp.getWriter().print(objectMapper.writeValueAsString(response));
            return null;
        }
        return new ModelAndView((String) response);
    }

}
