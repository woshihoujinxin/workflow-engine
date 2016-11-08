package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 查询车型
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"provinceCode", ""}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCode", ""}),
        @PGScheme(requestParamName = "parentId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"vehicleInfo.parentId", ""}),
        @PGScheme(requestParamName = "queryCode", strategy = StrategyType.CONTEXT, strategyNeedParams = {"vehicleInfo.vehicleFgwCode", ""}),
        @PGScheme(requestParamName = "seatCount", strategy = StrategyType.CONTEXT, strategyNeedParams = {"vehicleInfo.seat", ""}),
        @PGScheme(requestParamName = "carRequestType", strategy = StrategyType.FIXED, strategyNeedParams = "03"),
        @PGScheme(requestParamName = "licenseFlag", strategy = StrategyType.FIXED, strategyNeedParams = "1"),
        @PGScheme(requestParamName = "carModel", strategy = StrategyType.CONTEXT, strategyNeedParams = {"brandCode", ""}),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""}),
        @PGScheme(requestParamName = "enrollDate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"enrollDate", ""}),
        @PGScheme(requestParamName = "frameNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "engineNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"engineNo", ""}),
        @PGScheme(requestParamName = "vinNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""})
})
public class VehicleChecked extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(VehicleChecked.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/carSelect/vehicleChecked";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.sendPostRequest(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(realResponse);
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("车型校验通过");
//                context.put("countryNature", getStringNodeByKey(jsonNode, "countryNature"));
            context.put("seatFlag", "1".equals(JacksonUtil.getStringNodeByKey(jsonNode, "seatFlag")) ? "1" : "0");
            return BusinessUtil.successfulStepState(this);
        } else if ("4".equals(resultCode)) {
            logger.info("您所选取的车型有误，请仔细核对您的行驶证原件后重新选择");
            return BusinessUtil.failureStepState(this, MsgEnum.QueryCar_WrongVehicleInfo);
        } else {
            logger.info("车型校验失败,响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        logger.info("车型不能通过校验,请修改品牌型号");
        return BusinessUtil.failureStepState(this, MsgEnum.QueryCar_WrongBrandCode);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

