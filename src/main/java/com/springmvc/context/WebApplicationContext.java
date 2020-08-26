package com.springmvc.context;

import com.springmvc.annotation.*;
import com.springmvc.exception.ContextException;
import com.springmvc.proxy.JdkProxy;
import com.springmvc.xml.XmlParser;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebApplicationContext  {

    private String contextConfigLocation;
    private List<String> classNames = new ArrayList<>();
    // Spring的IOC容器
    public Map<String, Object> iocMap = new ConcurrentHashMap<>();

    public WebApplicationContext(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    /**
     * 初始化
     */
    public void refresh(){
        String basePackage = XmlParser.getBasePackage(contextConfigLocation.split(":")[1]);
        assert basePackage != null;
        String[] basePackages = basePackage.split(",");
        if(basePackages.length > 0) {
            for(String pack: basePackages) {
                // com.example.controller
                // com.example.service
                try {
                    // 扫描包
                    executeScanPackage(pack.trim());
                    // 实例化对象
                    executeInstance();
                    // 实例化Spring容器中bean对象
                    executeAutowired();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("扫描的包：" + classNames);
            System.out.println("ioc容器中的对象：" + iocMap);
        }
    }

    /**
     *
     * @param pack 扫描包
     */
    public void executeScanPackage(String pack) {
        URL url = this.getClass().getClassLoader().getResource("/" + pack.replaceAll("\\.", "/"));
        //  路径：/com/example/controller
        assert url != null;
        String path = url.getFile();
        File dir = new File(path);
        for(File f: Objects.requireNonNull(dir.listFiles())) {
            if(f.isDirectory()) {
                // 文件夹
                executeScanPackage(pack+"."+f.getName());
            } else {
                // 文件
                String className = pack+"."+f.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    /**
     * 实例化bean对象
     */
    public void executeInstance() {
        if(classNames.size() == 0) {
            // 没有要实例化的对象
            throw new ContextException("没有要实例化的class");
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(Controller.class)) {
                    // 控制层的类 com.springmvc.controller.UserController
                    // userController 控制层对象的名字
                    // System.out.println(clazz.getSimpleName());
                    String beanName = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
                    iocMap.put(beanName, clazz.newInstance());
                } else if(clazz.isAnnotationPresent(Service.class)) {
                    // com.springmvc.controller.UserServiceImpl
                    Service serviceAnnotation = clazz.getAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();
                    if("".equals(beanName)) {
                        // 取当前Service类的接口
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> c1 : interfaces) {
                            beanName = c1.getSimpleName().substring(0, 1).toLowerCase() + c1.getSimpleName().substring(1);
                            iocMap.put(beanName, clazz.newInstance());
                        }
                    } else {
                        // 在Service(value="us") beanName = us
                        iocMap.put(beanName, clazz.newInstance());
                    }
                } else if(clazz.isAnnotationPresent(Aspect.class)) {
                    Method[] aopMethods = clazz.getDeclaredMethods();
                    for (Method method : aopMethods) {
                        if(method.isAnnotationPresent(Around.class)) {
                            Around around = method.getAnnotation(Around.class);
                            String value = around.value();

                            //被代理对象的方法的名字
                            String methodName = value.substring(value.lastIndexOf(".")+1);
                            String obj = value.substring(0, value.lastIndexOf("."));

                            // 被代理对象
                            Class<?> aClass = Class.forName(obj);
                            String superInterface = aClass.getGenericInterfaces()[0].getTypeName();
                            String name = superInterface.substring(superInterface.lastIndexOf(".") + 1);
                            String beanName = name.substring(0, 1).toLowerCase() + name.substring(1);

                            // 封装JdkProxy对象 返回被代理的对象的实例
                            JdkProxy proxy = new JdkProxy(aClass, clazz, methodName, method);
                            Object instance = proxy.getInstance();

                            // 替换原来的iocMap中的对象
                            iocMap.put(beanName, instance);

                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 实现spring容器中bean的注入(自动装配)
     */
    private void executeAutowired() throws IllegalAccessException {

        if(iocMap.isEmpty()) {
            throw new ContextException("没有找到初始化bean对象");
        }

        for (Map.Entry<String, Object> entry : iocMap.entrySet()) {
            String key = entry.getKey();
            Object bean = entry.getValue();
            Field[] declaredFields =  bean.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                // 如果字段有Autowired的注解
                if(declaredField.isAnnotationPresent(Autowired.class)) {
                    // 先判断是否有value
                    Autowired autowired = declaredField.getAnnotation(Autowired.class);
                    String beanName = autowired.value();
                    if("".equals(beanName)) {
                        Class<?> type = declaredField.getType();
                        beanName = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);
                    }
                    declaredField.setAccessible(true);
                    // 属性注入 调用反射给属性注入值
                    declaredField.set(bean, iocMap.get(beanName));
                }
            }
        }
    }

}
