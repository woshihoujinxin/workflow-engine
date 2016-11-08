package com.workflow.engine.core.pingan.steps;


import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.pingan.config.Constants;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.pingan.utils.PinganBizUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
public class ForceQuote implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(ForceQuote.class);
    private static final String _API_FORCE_QUOTE = "http://u.pingan.com/autox/do/api/force-quote";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        if (!BusinessUtil.checkCompulsoryIsChoosed((InsurancePackageVO) context.get("insurancePackage"))) {
            logger.info("套餐中不包含交强险,直接跳过此步骤!");
            return BusinessUtil.successfulStepState(this);
        }
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_FORCE_QUOTE, requestParams, PinganBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(response);
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
            logger.info("交强险报价失败,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    private Map<String, String> getRequestParams(Map<String, Object> context) {
        String flowId = (String) context.get("flowId");
        String xrc = (String) context.get("xrc");
        String forceBeginDate = (String) context.get("forceBeginDate");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("flowId", flowId);
        requestParams.put("__xrc", xrc);
        requestParams.put("responseProtocol", "json");
        requestParams.put("forceInfo.beginDate", forceBeginDate);
        return requestParams;
    }

}

