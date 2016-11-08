package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JsoupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 获取SessionID
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "head.requestType", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "head.requestCode", strategy = StrategyType.FIXED, strategyNeedParams = "20132001"),
        @PGScheme(requestParamName = "head.uuid", strategy = StrategyType.FIXED, strategyNeedParams = "1234"),
        @PGScheme(requestParamName = "head.sessionId", strategy = StrategyType.FIXED, strategyNeedParams = "first"),
        @PGScheme(requestParamName = "head.channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "citySelect", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "netAddress", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "carInfo.ccaFlag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "carInfo.ccaEntryId", strategy = StrategyType.FIXED, strategyNeedParams = "")
})
public class GetSessionId extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(GetSessionId.class);


    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/car/carInput1";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.sendPostRequest(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        String sessionId = JsoupHelper.getStringById(realResponse, "sessionId");
        if (sessionId != null && !"".equals(sessionId)) {
            logger.info("成功获取到sessionId: {}", sessionId);
            context.put("sessionId", sessionId);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("无法获取到sessionId, 流程终止. 当前步骤请求的响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
