package com.workflow.engine.core.common.strategy;

import java.util.Map;

/**
 * 请求参数生成策略接口
 *
 * Created by houjinxin on 16/5/30.
 */
public interface IParamsGeneratorStrategy {

    /**
     * 生成参数的方法
     *
     * @param context 存储参数值的上下文
     * @param strategyNeedParams 请求参数名
     * @return 请求参数的值
     */
    String generatorParam(Map<String, Object> context, String[] strategyNeedParams) throws Exception;
}
