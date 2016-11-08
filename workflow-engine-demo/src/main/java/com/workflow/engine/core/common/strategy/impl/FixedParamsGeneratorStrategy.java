package com.workflow.engine.core.common.strategy.impl;

import com.workflow.engine.core.common.strategy.IParamsGeneratorStrategy;

import java.util.Map;

/**
 *
 * 针对需要写死值的参数进行处理
 * strategyNeedParams格式
 * {"请求参数值"}
 * Created by houjinxin on 16/5/30.
 */
public class FixedParamsGeneratorStrategy implements IParamsGeneratorStrategy {
    @Override
    public String generatorParam(Map<String, Object> context, String[] strategyNeedParams) throws Exception {
        //下划线表示空字符
        return strategyNeedParams[0];
    }
}
