package com.workflow.engine.expression.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 运算符重载注解， 支持在Java对象的运算符重载。
 *
 * @author deonwu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperatorReload {
    public String sign() default "+";
}
