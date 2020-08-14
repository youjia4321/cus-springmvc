package com.springmvc.handler;

import com.alibaba.fastjson.JSON;
import com.springmvc.model.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerExecutionChain {

    public HandlerMapping handler;

    public HandlerExecutionChain(HandlerMapping handler) {
        this.handler = handler;
    }

    public ModelAndView handle(HttpServletRequest req) throws InvocationTargetException, IllegalAccessException {

        Method method = handler.getMethod();
        Object bean = handler.getController();
        // 获取参数
        Object[] parameters = new Object[handler.getArgs().length];
        for(int i = 0; i < handler.getArgs().length; i++) {
            parameters[i] = req.getParameter(handler.getArgs()[i]);
        }
        Object viewName = method.invoke(bean, parameters);
        return new ModelAndView((String) viewName);
    }

    public void handle(HttpServletRequest req, HttpServletResponse resp)
            throws InvocationTargetException, IllegalAccessException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        Method method = handler.getMethod();
        Object bean = handler.getController();
        // 获取参数
        Object[] parameters = new Object[handler.getArgs().length];
        for(int i = 0; i < handler.getArgs().length; i++) {
            parameters[i] = req.getParameter(handler.getArgs()[i]);
        }
        Object response =  method.invoke(bean, parameters);
        resp.getWriter().println(JSON.toJSONString(response.toString()));
    }

}
