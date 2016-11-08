package com.workflow.engine.core.common.strategy;

import com.workflow.engine.core.common.strategy.impl.ContextParamsGeneratorStrategy;
import com.workflow.engine.core.common.strategy.impl.FixedParamsGeneratorStrategy;
import com.workflow.engine.core.common.strategy.impl.MethodParamsGeneratorStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略注册
 * Created by houjinxin on 16/6/2.
 */
public class StrategyRegister {

    public static final Map<StrategyType, IParamsGeneratorStrategy> _PARAMS_GENERATOR_STRATEGY = new HashMap<StrategyType, IParamsGeneratorStrategy>() {{
        put(StrategyType.FIXED, new FixedParamsGeneratorStrategy());
        put(StrategyType.CONTEXT, new ContextParamsGeneratorStrategy());
        put(StrategyType.METHOD, new MethodParamsGeneratorStrategy());
    }};

}