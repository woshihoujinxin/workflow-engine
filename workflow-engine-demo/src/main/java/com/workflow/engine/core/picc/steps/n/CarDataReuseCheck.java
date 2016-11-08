package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 车辆数据可复用检查
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "licenseflag", strategy = StrategyType.FIXED, strategyNeedParams = "1"),
        @PGScheme(requestParamName = "licenseNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"licenseNo", ""})
})
public class CarDataReuseCheck extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(CarDataReuseCheck.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/car/carDataReuse";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(realResponse);
        String status = JacksonUtil.getStringNodeByKey(jsonNode, "status");
        if ("success".equals(status)) {
            String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
            logger.info("车辆数据{}", "1".equals(resultCode) ? "可复用" : "不可复用");
            if ("1".equals(resultCode)) {
                context.put("reuseCarData", true); //reuseCarData 用于决定车型的来源 若车型数据可复用直接复用,若不可复用选择直接查询
                return BusinessUtil.successfulStepState(this, "车辆数据可复用");
            } else { //resultCode=0
                context.put("reuseCarData", false);
                return BusinessUtil.successfulStepState(this, "车辆数据不可复用");
            }
        } else {
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

