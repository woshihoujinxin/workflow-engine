package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 续保检查
 * Created by houjinxin on 16/3/9.
 */
public class RenewalCheck implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(RenewalCheck.class);

    private static final String _API_IS_RENEWAL = "http://www.epicc.com.cn/wap/carProposal/renewal/isRenewal";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_IS_RENEWAL, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(response);
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("3".equals(resultCode)) {
            logger.info("该车为转保车辆");
            context.put("isRenewal", "0"); //不是转保用户
            return BusinessUtil.successfulStepState(this, "转保");
        } else if ("1".equals(resultCode)) {
            logger.info("该车为续保车辆");
            context.put("isRenewal", "1"); //是续保用户
            return BusinessUtil.successfulStepState(this, "续保");
        } else {
            logger.info("续保检查失败,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String licenseNo = (String) context.get("licenseNo");
        String citySelected = (String) context.get("cityCode");
        Map<String, String> requestParams = new HashMap<String, String>();

        requestParams.put("channelNo", "2");
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("citySelected", citySelected);
        requestParams.put("ccaFlag", "");
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

