package com.workflow.engine.core.pingan.steps;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getJsonNode;
import static com.workflow.engine.core.common.utils.JacksonUtil.getMapNodeByKey;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;
import static com.workflow.engine.core.common.utils.StepUtil.generateParams;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0000;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C3003;
import static com.workflow.engine.core.pingan.utils.PinganBizUtil.getHeaders;

/**
 * 商业险报价
 * Created by houjinxin on 16/3/9.
 */
public class BizQuote implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(BizQuote.class);

    private static final String _API_BIZ_QUOTE = "http://u.pingan.com/autox/do/api/biz-quote";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = generateParams(context, this);
        String response = executePostMethod(_API_BIZ_QUOTE, requestParams, getHeaders());
        JsonNode jsonNode = getJsonNode(response);
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if (resultCode != null && resultCode.equals(_RESULT_CODE_C0000)) {
            Map<String, String> circResult = getMapNodeByKey(jsonNode, "circResult");
            if(_RESULT_CODE_C3003.equals(circResult.get("resultCode"))) { //商业险提前投保
                String earliestBizBeginDate = (String) context.get("bizBeginDate");
                context.put("bizCanApply", false); //商业险不可投保
                context.put("earliestBizBeginDate", earliestBizBeginDate);
                MsgEnum msgEnum = MsgEnum.BizFailure;
                msgEnum.setMsg("商业险未到投保日期, 最早投保日期为" + earliestBizBeginDate);
                context.put("bizFailure", msgEnum);
                logger.info("商业险未到投保日期, 最早投保日期为{}", earliestBizBeginDate);
                return successfulStepState(this);
            } else {
                Map bizPremium = getMapNodeByKey(jsonNode, "bizPremium");
                context.put("bizPremium", bizPremium);
                logger.info("商业险报价成功,结果为:\n{}", bizPremium);
                return successfulStepState(this);
            }
        } else {
            logger.info("商业险报价失败,响应为:\n{}", response);
            return failureStepState(this);
        }
    }

}

