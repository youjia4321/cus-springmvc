package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // 元注解 定义范围
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

    String value() default "";

}
