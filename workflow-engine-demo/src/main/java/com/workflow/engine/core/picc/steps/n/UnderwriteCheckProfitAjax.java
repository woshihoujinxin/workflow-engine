package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;

/**
 * 核保
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "lastcarownername", strategy = StrategyType.CONTEXT, strategyNeedParams = {"carOwner", ""}),
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "areaCodeLast", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "cityCodeLast", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "startdate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"startDate", ""}),
        @PGScheme(requestParamName = "starthour", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "enddate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"endDate", ""}),
        @PGScheme(requestParamName = "endhour", strategy = StrategyType.FIXED, strategyNeedParams = "24"),
        @PGScheme(requestParamName = "licenseno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"licenseNo", ""}),
        @PGScheme(requestParamName = "engineno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"engineNo", ""}),
        @PGScheme(requestParamName = "vinno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "frameno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "seatcount", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.seat", "5"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"vehicleInfo.seat", "5"}),
        @PGScheme(requestParamName = "carOwner", strategy = StrategyType.CONTEXT, strategyNeedParams = {"carOwner", ""}),
        @PGScheme(requestParamName = "isRenewal", strategy = StrategyType.CONTEXT, strategyNeedParams = {"isRenewal", ""}),
        @PGScheme(requestParamName = "enrolldate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"enrollDate", ""}),
        @PGScheme(requestParamName = "guohuselect", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "licenseflag", strategy = StrategyType.FIXED, strategyNeedParams = "1"),
        @PGScheme(requestParamName = "isOutRenewal", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050200", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050210", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050500", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "seatflag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "transferdate", strategy = StrategyType.METHOD, strategyNeedParams = "$formatDate($SYSDATE(),\"yyyy-MM-dd\")"),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""}),
        @PGScheme(requestParamName = "ccaID", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaEntryId", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaFlag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "lastdamagedbi", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "guohuflag", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "runAreaCodeName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "assignDriver", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "haveLoan", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "LoanName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "weiFaName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "carDrivers", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "oldPolicyNo", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "interimNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"interimNo", ""})
})
public class UnderwriteCheckProfitAjax extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(UnderwriteCheckProfitAjax.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/underWrite/underwriteCheckProfitAjax";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(realResponse));
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("核保成功,响应为:\n{}", realResponse);
            return successfulStepState(this);
        } else if ("2".equals(resultCode) && "请稍候重试".equals(getStringNodeByKey(jsonNode, "resultMsg"))) {
            logger.info("该车暂不支持网上投保,请联系客服!");
            return failureStepState(this, MsgEnum.CannotQuoteOnLine);
        } else if ("4".equals(resultCode) && "温馨提示：网销产品只能适用家庭自用客车".equals(getStringNodeByKey(jsonNode, "resultMsg"))) {
            logger.info("网销产品只能适用家庭自用客车!");
            return failureStepState(this, MsgEnum.NonHomeCar);
        } else {
            logger.info("核保失败,响应为: \n{}", realResponse);
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

