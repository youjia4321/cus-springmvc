package com.springmvc.servlet;

import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.annotation.RequestParam;
import com.springmvc.context.WebApplicationContext;
import com.springmvc.exception.ContextException;
import com.springmvc.handler.HandlerMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    private WebApplicationContext webApplicationContext;
    // 存储URI和对象的方法映射关系
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // classpath: springmvc.xml 读取初始化的参数
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        // System.err.println(contextConfigLocation);
        // 创建spring容器
        webApplicationContext = new WebApplicationContext(contextConfigLocation);
        // 初始化spring容器
        webApplicationContext.refresh();
        // 初始化请求映射 /user/query ----> Controller ----> method ----> parameters
        initHandlerMappings();
        System.out.println("请求地址和控制器方法的映射关系：" + handlerMappings);

    }

    /**
     * 初始化请求映射
     */
    private void initHandlerMappings() {
        // 判断容器中是否有bean对象
        if(webApplicationContext.iocMap.isEmpty()) {
            throw new ContextException("Spring容器为空");
        }
        for (Map.Entry<String, Object> entry: webApplicationContext.iocMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            Object ctl = entry.getValue();
            if(clazz.isAnnotationPresent(Controller.class)) {
                parseHandlerFromController(clazz, ctl);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 进行请求的分发处理
        executeDispatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * 进行请求分发
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     */
    private void executeDispatch(HttpServletRequest req, HttpServletResponse resp) {
        HandlerMapping handler = getHandler(req);
            try {
                if(handler != null) {
                    handler.handle(req, resp);
                } else {
                    resp.getWriter().print("<h1>404 Not Found</h1>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * 获取handlerMapping
     * @param req HttpServletRequest
     * @return HandlerMapping
     */
    private HandlerMapping getHandler(HttpServletRequest req) {
        // /get/user
        String requestURI = req.getRequestURI();
        for (HandlerMapping handlerMapping : handlerMappings) {
            if(handlerMapping.getUri().equals(requestURI)) {
                return handlerMapping;
            }
        }
        return null;
    }

    /**
     * 通过控制器类解析请求映射
     * @param clazz Class
     */
    private void parseHandlerFromController(Class<?> clazz, Object ctl) {
        //如果controller类包含RequestMapping注解
        String uriPrefix = "";
        if(clazz.isAnnotationPresent(RequestMapping.class)){
            uriPrefix = clazz.getDeclaredAnnotation(RequestMapping.class).value();
        }
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if(declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                String URI = uriPrefix + declaredMethod.getAnnotation(RequestMapping.class).value();
                // 处理请求参数
                List<String> paramNameList = new ArrayList<>();
                for(Parameter parameter: declaredMethod.getParameters()) {
                    if(parameter.isAnnotationPresent(RequestParam.class)) {
                        paramNameList.add(parameter.getDeclaredAnnotation(RequestParam.class).value());
                    } else {
                        paramNameList.add(parameter.getName());
                    }
                }
                System.out.println(paramNameList);
                String[] params = paramNameList.toArray(new String[paramNameList.size()]);
                HandlerMapping handlerMapping = new HandlerMapping(URI, ctl, declaredMethod, params);
                handlerMappings.add(handlerMapping);

            }

        }

    }
}
