package com.springmvc.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandlerMapping {

    private String uri;
    private Object controller;
    private Method method;
    private String[] args;
    private boolean isResponseBody;

}
