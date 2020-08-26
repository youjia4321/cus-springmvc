package com.springmvc.proxy;

import com.springmvc.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.Connection;

public class CglibProxy {

    public static Object getInstance(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();

        MethodInterceptor interceptor = new MethodInterceptor() {
            Connection root = null;
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                try {
                    root = TransactionManager.connection();
                    root.setAutoCommit(false);
                    Object result = methodProxy.invokeSuper(o, args);
                    root.commit();
                    return result;
                } catch (Exception e) {
                    root.rollback();
                    throw new RuntimeException();
                }
            }
        };
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(interceptor);
        return enhancer.create();
    }

}
