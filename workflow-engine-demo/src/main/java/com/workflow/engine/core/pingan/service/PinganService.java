package com.workflow.engine.core.pingan.service;

import com.workflow.engine.core.pingan.config.CityCodeMappings;
import com.workflow.engine.core.pingan.config.FlowMappings;
import com.workflow.engine.core.pingan.config.HandlerMappings;
import com.workflow.engine.core.service.impl.AbstractThirdPartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 平安报价业务类
 * Created by houjinxin on 16/3/13.
 */
public class PinganService extends AbstractThirdPartyService {

    private static Logger logger = LoggerFactory.getLogger(PinganService.class);

    private static final Map<String, Object> baseContext = new HashMap<String, Object>(){{
        put("cityQuoteFlowMapping", FlowMappings._CITY_QUOTE_FLOW_MAPPING);
        put("cityFindVehicleFlowMapping", FlowMappings._CITY_FIND_VEHICLE_FLOW_MAPPING);
        put("stepCityPhMapping", HandlerMappings._STEP_CITY_PH_MAPPING);
        put("stepCityRhMapping", HandlerMappings._STEP_CITY_RH_MAPPING);
        put("cityCodeMapping", CityCodeMappings._CITY_CODE_MAPPINGS);
    }};

    @Override
    protected Map<String, Object> createBizContext(Object bizObject) {
        Map<String, Object> bizContext = super.createBizContext(bizObject);
        //默认该车不是过户车
        bizContext.put("transferYesOrNo", false);
        //默认车座数不为0
        bizContext.put("seatCountIsZero", false);
        return bizContext;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected Map<String, Object> getBaseContext() {
        return baseContext;
    }
}
