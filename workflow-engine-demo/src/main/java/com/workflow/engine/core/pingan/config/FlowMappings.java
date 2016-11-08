package com.workflow.engine.core.pingan.config;

import com.workflow.engine.core.common.flow.IFlow;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.pingan.config.Flows._FIND_VEHICLE_FLOW_TYPE1;
import static com.workflow.engine.core.pingan.config.Flows._QUOTE_FLOW_TYPE1;

/**
 * 城市与流程的映射关系
 * Created by houjinxin on 16/3/11.
 */
public class FlowMappings {

    /**
     * 报价流程,平安目前只有一个
     */
    public static final Map<String, IFlow> _CITY_QUOTE_FLOW_MAPPING = new HashMap<String, IFlow>() {{
        put("default", _QUOTE_FLOW_TYPE1);
        put("110100", _QUOTE_FLOW_TYPE1); //北京
        put("120100", _QUOTE_FLOW_TYPE1); //天津
        put("440100", _QUOTE_FLOW_TYPE1); //广州
        put("440300", _QUOTE_FLOW_TYPE1); //深圳
        put("500100", _QUOTE_FLOW_TYPE1); //重庆
        put("510100", _QUOTE_FLOW_TYPE1); //成都
    }};

    /**
     * 车型查询流程,平安保险公司只有一个
     */
    public static final Map<String, IFlow> _CITY_FIND_VEHICLE_FLOW_MAPPING = new HashMap<String, IFlow>() {{
        put("default", _FIND_VEHICLE_FLOW_TYPE1);
    }};
}
