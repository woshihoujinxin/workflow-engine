package com.workflow.engine.core.pingan.steps.n;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
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
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C3003;

/**
 * 商业险报价
 * Created by houjinxin on 16/3/9.
 */
public class BizQuote extends PinganAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(BizQuote.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/autox/do/api/biz-quote";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return null;
    }

    /**
     * 需用通过参数处理器来获取请求参数
     * @return
     */
    @Override
    protected boolean needParamsHandlerOrNot() {
        return true;
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
            Map<String, String> circResult = JacksonUtil.getMapNodeByKey(jsonNode, "circResult");
            if(_RESULT_CODE_C3003.equals(circResult.get("resultCode"))) { //商业险提前投保
                String earliestBizBeginDate = (String) context.get("bizBeginDate");
                context.put("bizCanApply", false); //商业险不可投保
                context.put("earliestBizBeginDate", earliestBizBeginDate);
                MsgEnum msgEnum = MsgEnum.BizFailure;
                msgEnum.setMsg("商业险未到投保日期, 最早投保日期为" + earliestBizBeginDate);
                context.put("bizFailure", msgEnum);
                logger.info("商业险未到投保日期, 最早投保日期为{}", earliestBizBeginDate);
                return BusinessUtil.successfulStepState(this);
            } else {
                Map bizPremium = JacksonUtil.getMapNodeByKey(jsonNode, "bizPremium");
                context.put("bizPremium", bizPremium);
                logger.info("商业险报价成功,结果为:\n{}", bizPremium);
                return BusinessUtil.successfulStepState(this);
            }
        } else {
            logger.info("商业险报价失败,响应为:\n{}", realResponse);
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

