package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 续保信息查询
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams ={ "cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams ={ "cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "beforeProposalNo", strategy = StrategyType.CONTEXT, strategyNeedParams ={ "identity", ""}),
        @PGScheme(requestParamName = "licenseNo", strategy = StrategyType.CONTEXT, strategyNeedParams ={ "licenseNo", ""}),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams ={ "sessionId", ""})
})
public class QueryRenewal extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(QueryRenewal.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/renewal/queryRenewal";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("proSelected")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.areaCode", "11000000"),
                ParamGeneratorUtil.schemeParam("citySelected")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.cityCode", "11000000"),
                ParamGeneratorUtil.schemeParam("beforeProposalNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("identity", ""),
                ParamGeneratorUtil.schemeParam("licenseNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("licenseNo", ""),
                ParamGeneratorUtil.schemeParam("sessionId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("sessionId", "")
        );
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
            Map<String, Object> vehicleInfo = new HashMap<>();
            Map<String, String> appliCarInfo = JacksonUtil.getMapNodeByKey(jsonNode, "appliCarInfo");
            Map<String, String> appliInfo = JacksonUtil.getMapNodeByKey(jsonNode, "appliInfo");
            Map<String, String> insuredInfo = JacksonUtil.getMapNodeByKey(jsonNode, "insuredInfo");
            vehicleInfo.put("appliCarInfo", appliCarInfo);
            vehicleInfo.put("appliInfo", appliInfo);
            vehicleInfo.put("insuredInfo", insuredInfo);
            context.put("vehicleInfo", vehicleInfo);
            context.put("reuseCarData", true);
            logger.info("获取续保信息成功,车型信息可复用");
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("续保信息查询失败,响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        if ("2".equals(realResponse) || "3".equals(realResponse) || "4".equals(realResponse)) {
            logger.info("证件号或上年保单号不匹配");
            return BusinessUtil.failureStepState(this, MsgEnum.WrongIdentity);
        } else if ("7".equals(realResponse)) {
            logger.info("暂不支持网上投保!", realResponse);
            return BusinessUtil.failureStepState(this, MsgEnum.CannotQuoteOnLine);
        } else {
            logger.info("未知原因出错,联系客服人工报价!,响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this, MsgEnum.Other);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

