package com.springmvc.proxy;

import com.springmvc.utils.ProceedingJoinPoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//声明jdk动态代理类
public class JdkProxy {
    //被代理对象
    Class<?> aClass;
    //切面对象
    Class<?> clazz;
    //被代理对象的方法的名字,本框架只能代理一个方法
    String methodName;
    //切面对象的方法
    Method aopMethod;

    public JdkProxy(Class<?> aClass, Class<?> clazz, String methodName,  Method aopMethod){
        this.aClass=aClass;
        this.clazz=clazz;
        this.methodName=methodName;
        this.aopMethod=aopMethod;
    }

    public Object getInstance(){
        return Proxy.newProxyInstance(aClass.getClassLoader(), aClass.getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //method就是被代理的方法
                        if(method.getName().equals(methodName)){ // 如果相等，就说明是被代理的方法
                            ProceedingJoinPoint proceedingJoinPoint = new ProceedingJoinPoint(method, args, aClass.newInstance());
                            //执行切面的方法
                            return aopMethod.invoke(clazz.newInstance(), proceedingJoinPoint);
                        }
                        //如果方法名不相同，则说明是不被代理的方法，则不用代理对象去处理
                        return method.invoke(aClass.newInstance(), args);
                    }
                });
    }

}