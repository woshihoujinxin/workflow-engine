package com.workflow.engine.core.picc.service;

import com.workflow.engine.core.picc.config.CityCodeMappings;
import com.workflow.engine.core.picc.config.FlowMappings;
import com.workflow.engine.core.picc.config.HandlerMappings;
import com.workflow.engine.core.service.impl.AbstractThirdPartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * PICC报价服务
 * Created by houjinxin on 16/3/13.
 */
public class PiccService extends AbstractThirdPartyService {

    private static final Logger logger = LoggerFactory.getLogger(PiccService.class);

    /**
     * 与流程机制相关的上下文配置, 主要包括城市流程映射, 步骤城市RH映射, 步骤城市PH映射, 标准城市代码与保险公司城市代码映射
     */
    private static final Map<String, Object> baseContext = new HashMap<String, Object>(){{
        put("cityQuoteFlowMapping", FlowMappings._CITY_QUOTE_FLOW_MAPPING);
        put("stepCityPhMapping", HandlerMappings._STEP_CITY_PH_MAPPING);
        put("stepCityRhMapping", HandlerMappings._STEP_CITY_RH_MAPPING);
        put("cityCodeMapping", CityCodeMappings._CITY_CODE_MAPPINGS);
    }};

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected Map<String, Object> getBaseContext() {
        return baseContext;
    }
}
