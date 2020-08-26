package com.springmvc.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProceedingJoinPoint {

    private Method method;

    private Object[] args;

    private Object obj;

    public Object proceed() throws Exception {
        return method.invoke(obj, args);
    }

}
