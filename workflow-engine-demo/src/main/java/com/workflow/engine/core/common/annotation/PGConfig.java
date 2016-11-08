package com.workflow.engine.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于接收多个参数配置方案的注解
 * Created by houjinxin on 16/6/27.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PGConfig {

    PGScheme[] value() default {};
}
