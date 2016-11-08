package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.sendPostRequest;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getListNodeByKey;

/**
 * 查询车型
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"provinceCode", ""}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCode", ""}),
        @PGScheme(requestParamName = "queryCode", strategy = StrategyType.CONTEXT, strategyNeedParams = {"brandCode", ""}),
        @PGScheme(requestParamName = "licenseNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"licenseNo", ""}),
        @PGScheme(requestParamName = "frameNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "engineNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"engineNo", ""}),
        @PGScheme(requestParamName = "enrollDate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"enrollDate", ""}),
        @PGScheme(requestParamName = "vinNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "isRenewal", strategy = StrategyType.CONTEXT, strategyNeedParams = {"isRenewal", ""}),
        @PGScheme(requestParamName = "licenseFlag", strategy = StrategyType.FIXED, strategyNeedParams = "1"),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""})
})
public class VehicleFind extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(VehicleFind.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/carSelect/vehicleFind";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return sendPostRequest(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(realResponse);
        List<Map<String, String>> vehicles = getListNodeByKey(jsonNode, "");
        //TODO:车型查询完成后要做个判断,若果存在车型的唯一标识,那么就符合该标识的车型,否则将车型列表推送至前端
        logger.info("按照该品牌型号查询车型成功,选择列表中第一辆车");
        context.put("reuseCarData", false);
        Map<String, String> vehicleInfo = vehicles.get(0);
        context.put("vehicleInfo", vehicleInfo);
        logger.info("车型信息为:\n{}", vehicleInfo);
        return successfulStepState(this);
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        logger.info("品牌型号不正确,请重新输入");
        return failureStepState(this, MsgEnum.QueryCar_WrongBrandCode);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

