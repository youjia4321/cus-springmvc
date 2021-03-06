package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE) // 元注解 定义范围
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    String value() default "";

}
