package com.workflow.engine.core.common.rh.impl;

import com.workflow.engine.core.common.rh.IParamsHandler;

import java.util.Map;

/**
 * 默认的参数生成器
 * Created by houjinxin on 16/3/11.
 */
public class DefaultParamsHandler implements IParamsHandler {
    @Override
    public Map<String, String> generateParams(Map<String, Object> context) {
        return generateParams(context, null);
    }

    @Override
    public Map<String, String> generateParams(Map<String, Object> context, Object... others) {
        return null;
    }
}
