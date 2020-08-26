package com.example.aop;

import com.springmvc.annotation.Around;
import com.springmvc.annotation.Aspect;
import com.springmvc.utils.ProceedingJoinPoint;

import java.util.Arrays;

@Aspect
public class MyAop {

    @Around("com.example.service.impl.UserServiceImpl.show")
    // 指定某个方法为切面方法，代理原(show)方法
    public Object around(ProceedingJoinPoint pjp) throws Exception {

        Object[] args = pjp.getArgs();
        String name = pjp.getMethod().getName();

        Object proceed = null;
        try {
            // 就是利用反射调用目标方法，就是method.invoke(obj, args)
            // @Before
            System.out.println("[MyAop]【环绕前置通知】【"+name+"】方法开始执行，用的参数列表："+ Arrays.asList(args));
            proceed = pjp.proceed();
            // @AfterReturning
            System.out.println("[MyAop]【环绕返回通知】【"+name+"】方法执行完成，返回通知，计算结果："+proceed);
        } catch (Exception e) {
            // @AfterThrowing
            System.out.println("[MyAop]【环绕异常通知】【"+name+"】方法出现了异常，异常信息：："+e);
            // 为了让外界知道这个异常，让这个异常一定抛出去
            throw new RuntimeException(e);
        } finally {
            // @After
            System.out.println("[MyAop]【环绕后置通知】【"+name+"】方法最终结束了");
        }

        return proceed;
    }

}
