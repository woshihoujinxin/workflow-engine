package com.workflow.engine.core.picc.config;

import com.workflow.engine.core.common.rh.IParamsHandler;
import com.workflow.engine.core.common.rh.IResponseHandler;
import com.workflow.engine.core.picc.steps.GetSessionId;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于配置步骤与地区和请求参数的映射关系
 * Created by houjinxin on 16/3/11.
 */
public class HandlerMappings {

    public static final Map<String, Map<String, IParamsHandler>> _STEP_CITY_PH_MAPPING = new HashMap<String, Map<String, IParamsHandler>>(){{
        put(GetSessionId.class.getName(),  new HashMap<String, IParamsHandler>(){{
        }});
    }};

    public static final Map<String, Map<String, IResponseHandler>> _STEP_CITY_RH_MAPPING = new HashMap<String, Map<String, IResponseHandler>>(){{
        put(GetSessionId.class.getName(),  new HashMap<String, IResponseHandler>(){{
        }});
    }};

}
