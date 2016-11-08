package com.workflow.engine.core.common.annotation;

import com.workflow.engine.core.common.strategy.StrategyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于配置请求中某一参数的生成方案
 * Created by houjinxin on 16/6/27.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PGScheme {

    //请求参数名称
    String requestParamName();

    //(值源表达式)判定取值来源的逻辑表达式
    String valueSourceExpression() default "";

    //不需要值源表达式时,取值的策略
    StrategyType strategy() default StrategyType.NONE;

    //值源表达式为真时的取值策略
    StrategyType strategyIfTrue() default StrategyType.NONE;

    //值源表达式为假时的取值策略
    StrategyType strategyIfFalse() default StrategyType.NONE;

    //不配置值源表达式时的策略参量
    String[] strategyNeedParams() default {};

    //值源表达式为真时策略参量
    String[] strategyNeedParamsIfTrue() default {};

    //值源表达式为假时策略参量
    String[] strategyNeedParamsIfFalse() default {};

    //满足个性化的需求的配置
    //个性化值源表达式
    String individualValueSourceExpression() default "";

    //个性化取值策略
    StrategyType individualStrategy() default StrategyType.NONE;

    //个性化策略参量
    String[] individualStrategyNeedParams() default {};

}
