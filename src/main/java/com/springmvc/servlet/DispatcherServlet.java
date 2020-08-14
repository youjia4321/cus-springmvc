package com.springmvc.servlet;

import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.annotation.RequestParam;
import com.springmvc.annotation.ResponseBody;
import com.springmvc.context.WebApplicationContext;
import com.springmvc.exception.ContextException;
import com.springmvc.handler.HandlerExecutionChain;
import com.springmvc.handler.HandlerMapping;
import com.springmvc.model.ModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
                // 处理有@Controller注解的类
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
        HandlerExecutionChain handler = getHandlerInfo(req);
        try {
            if(handler != null) {
                if(!handler.handler.isResponseBody()) {
                    ModelAndView mv = handler.handle(req);
                    render(mv, req, resp);
                } else {
                    // 加了@ResponseBody注解的处理
                    handler.handle(req, resp);
                }
            } else {
                noHandlerFound(req, resp);
            }
        } catch (InvocationTargetException | IllegalAccessException | ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取handlerMapping
     * @param requestURI URI
     * @return HandlerMapping
     */
    private HandlerMapping getHandler(String requestURI) {
        // /get/user
        for (HandlerMapping handlerMapping : handlerMappings) {
            if(handlerMapping.getUri().equals(requestURI)) {
                return handlerMapping;
            }
        }
        return null;
    }

    /**
     * 获取请求映射执行链
     * @param req HttpServletRequest
     * @return HandlerExecutionChain
     */
    private HandlerExecutionChain getHandlerInfo(HttpServletRequest req) {
        HandlerMapping handler = getHandler(req.getRequestURI());
        if(handler == null) {
            return null;
        }
        return new HandlerExecutionChain(handler);
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
            // 判断是否有@ResponseBody注解
            boolean isResponseBody = false;
            if(declaredMethod.isAnnotationPresent(ResponseBody.class)) {
                isResponseBody = true;
            }
            if(declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = declaredMethod.getAnnotation(RequestMapping.class);
                String URI = uriPrefix + requestMapping.value();
                // 处理请求参数
                List<String> paramNameList = new ArrayList<>();
                // System.out.println(Arrays.toString(declaredMethod.getParameters()));
                for(Parameter parameter: declaredMethod.getParameters()) {
                    if(parameter.isAnnotationPresent(RequestParam.class)) {
                        paramNameList.add(parameter.getDeclaredAnnotation(RequestParam.class).value());
                    } else {
                        paramNameList.add(parameter.getName());
                    }
                }
                int size = paramNameList.size();
                String[] params = paramNameList.toArray(new String[size]);
                HandlerMapping handlerMapping = new HandlerMapping(URI, ctl, declaredMethod, params, isResponseBody);
                handlerMappings.add(handlerMapping);
            }
        }
    }

    /***
     * 根据modelAndView渲染页面
     * @param mv 页面信息
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException 异常
     * @throws IOException 异常
     */
    private void render(ModelAndView mv, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String viewName = mv.getViewName();
        req.getRequestDispatcher("/WEB-INF/view/" + viewName + ".jsp").forward(req, resp);
    }

    /**
     * 没找到请求的处理
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @throws IOException IO异常
     */
    private void noHandlerFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        // resp.getWriter().print("<h1>404 Not Found</h1>");
    }
}
