package com.workflow.engine.core.pingan.steps.n;


import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.pingan.config.Constants;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0000;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C4003;

/**
 * 交强险和车船税报价
 * Created by houjinxin on 16/3/9.
 */
public class ForceQuote extends PinganAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(ForceQuote.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/autox/do/api/force-quote";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("flowId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("flowId", ""),
                ParamGeneratorUtil.schemeParam("__xrc")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("xrc", ""),
                ParamGeneratorUtil.schemeParam("responseProtocol")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("json"),
                ParamGeneratorUtil.schemeParam("forceInfo.beginDate")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("forceBeginDate", "")
        );
    }

    @Override
    protected boolean skipCurrentStepOrNot(Map<String, Object> context) {
        return !BusinessUtil.checkCompulsoryIsChoosed((InsurancePackageVO) context.get("insurancePackage"));
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(realResponse);
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if (resultCode != null && resultCode.equals(_RESULT_CODE_C0000)) {
            Map<String, String> forcePremium = JacksonUtil.getMapNodeByKey(jsonNode, "forcePremium");
            if (_RESULT_CODE_C0000.equals(forcePremium.get("resultCode"))) { //交强险报价成功
                context.put("forcePremium", forcePremium);
                logger.info("交强险报价成功,结果为:\n{}", forcePremium);
                return BusinessUtil.successfulStepState(this);
            } else if (_RESULT_CODE_C4003.equals(forcePremium.get("resultCode")) ||
                    Constants._RESULT_CODE_S0001.equals(forcePremium.get("resultCode"))) { //交强险未到投保日期报价失败
                String earliestForceBeginDate = forcePremium.get("forceInfo.beginDate");
                context.put("forceCanApply", false); //交强险不可投保
                context.put("earliestForceBeginDate", earliestForceBeginDate);
                MsgEnum msgEnum = MsgEnum.ForceFailure;
                msgEnum.setMsg("交强险未到投保日期, 最早投保日期为" + earliestForceBeginDate);
                context.put("forceFailure", msgEnum);
                logger.info("交强险未到投保日期, 最早投保日期为{}", earliestForceBeginDate);
                return BusinessUtil.successfulStepState(this);
            } else {
                logger.info("其他未知情况,暂定为失败,终止流程");
                return BusinessUtil.failureStepState(this);
            }
        } else {
            logger.info("交强险报价失败,响应为:\n{}", realResponse);
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

