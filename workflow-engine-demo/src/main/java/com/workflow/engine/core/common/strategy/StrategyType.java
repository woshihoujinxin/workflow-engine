package com.workflow.engine.core.common.strategy;

/**
 * 参数生成策略以及每种策略执行所需参数的个数定义
 * Created by houjinxin on 16/6/12.
 */
public enum StrategyType {
    NONE(0), //用于表示策略的默认值(注解中使用)
    NULL(0), //空值
    FIXED(1), //固定参数
    CONTEXT(2), //上下文中取值
    METHOD(1); //方法取值

    public int strategyNeedParamsCount;

    StrategyType(int paramCount) {
        this.strategyNeedParamsCount = paramCount;
    }

    public int getStrategyNeedParamsCount() {
        return strategyNeedParamsCount;
    }
}
