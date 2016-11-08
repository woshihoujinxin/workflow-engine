package com.workflow.engine.core.common.rh.impl;

import com.workflow.engine.core.common.rh.IParamsHandler;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;

import java.util.Map;

import static com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil.generatorParamByStrategy;

/**
 * 抽象的参数处理器,不同城市的相同请求,参数不同时只需要配置步骤需要参数的获取方式即可
 * Created by houjinxin on 16/6/2.
 */
public abstract class AbstractParamsHandler implements IParamsHandler {

    /**
     * 定义该步骤需要哪些参数
     *
     * @return
     */
    protected abstract ParamGeneratorConfig stepNeededParamsConfig();

    @Override
    public Map<String, String> generateParams(Map<String, Object> context) throws Exception {
        return generateParams(context, null);
    }

    @Override
    public Map<String, String> generateParams(Map<String, Object> context, Object... others) throws Exception {
        return generatorParamByStrategy(context, stepNeededParamsConfig(), this);
    }
}
