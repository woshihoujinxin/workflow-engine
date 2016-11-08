package com.workflow.engine.core.picc.config;

import com.workflow.engine.core.common.flow.IFlow;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.picc.config.Flows._QUOTE_FLOW_TYPE3;

/**
 * 城市与流程的映射关系
 * Created by houjinxin on 16/3/11.
 */
public class FlowMappings {
    public static final Map<String, IFlow> _CITY_QUOTE_FLOW_MAPPING = new HashMap<String, IFlow>() {{
        put("default", _QUOTE_FLOW_TYPE3);
        put("110100", _QUOTE_FLOW_TYPE3); //北京
        put("120100", _QUOTE_FLOW_TYPE3); //天津
        put("440100", _QUOTE_FLOW_TYPE3); //广州
        put("440300", _QUOTE_FLOW_TYPE3); //深圳
        put("500100", _QUOTE_FLOW_TYPE3); //重庆
        put("510100", _QUOTE_FLOW_TYPE3); //成都
    }};
}
