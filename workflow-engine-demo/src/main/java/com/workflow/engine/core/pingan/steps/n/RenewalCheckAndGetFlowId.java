package com.workflow.engine.core.pingan.steps.n;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.strategy.ParamGeneratorUtil.configParamScheme;
import static com.workflow.engine.core.common.strategy.ParamGeneratorUtil.schemeParam;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.sendGetRequest;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getJsonNode;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0000;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0001;

/**
 * 续保检查以及获取FlowId
 * Created by houjinxin on 16/3/9.
 */
public class RenewalCheckAndGetFlowId extends PinganAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(RenewalCheckAndGetFlowId.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/autox/do/api/renewal-check?";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return configParamScheme(
                schemeParam("department.cityCode")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityAreaCode", "110100"),
                schemeParam("vehicle.licenseNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("licenseNo", ""),
                schemeParam("partner.mediaSources")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("sc03-direct-mpingan"),
                schemeParam("partner.partnerName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("chexian-mobile")
        );
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return sendGetRequest(requestUrl, requestParams, headers);
    }

    /**
     * 此处用于获取xrc
     *
     * @param context
     * @param response
     */
    @Override
    protected void getNecessaryParamsFromHttpResponse(Map<String, Object> context, HttpResponse response) {
        Header[] responseHeaders = response.getAllHeaders();
        String xrc = null;
        for (Header header : responseHeaders) {
            if (header.getName().trim().equals("__xrc")) {
                xrc = header.getValue();
            }
        }
        context.put("xrc", xrc);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        if (context.get("xrc") == null) {
            logger.info("获取xrc失败,流程终止");
            return failureStepState(this);
        }
        JsonNode jsonNode = getJsonNode(realResponse);
        String flowId = getStringNodeByKey(jsonNode, "flowId");
        if (flowId != null) { //获取到FlowId
            context.put("flowId", flowId);
            String resultCode = getStringNodeByKey(jsonNode, "resultCode");
            if (resultCode.equals(_RESULT_CODE_C0000)) {
                context.put("isRenewal", false);
                logger.info("该车是转保车辆");
                return successfulStepState(this);
            } else if (resultCode.equals(_RESULT_CODE_C0001)) {
                context.put("isRenewal", true);
                logger.info("该车是续保车辆");
                return successfulStepState(this);
            } else {
                logger.info("未知的车辆类型, 响应为:{}", realResponse);
                return failureStepState(this);
            }
        } else {
            logger.info("未能获取FlowId, 响应为:{}", realResponse);
            return failureStepState(this);
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
