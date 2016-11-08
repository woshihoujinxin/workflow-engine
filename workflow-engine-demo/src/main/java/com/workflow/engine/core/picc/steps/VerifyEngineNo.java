package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;

/**
 * 验证发动机号
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class VerifyEngineNo implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(VerifyEngineNo.class);

    private static final String _API_VERIFY_ENGINE_NO = "http://www.epicc.com.cn/wap/carProposal/Verify/verifyEngineNo";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_VERIFY_ENGINE_NO, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("发动机号验证成功:\n{}", response);
            return successfulStepState(this);
        } else {
            logger.info("发动机号验证失败,响应为:\n{}", response);
            return failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String engineNo = (String) context.get("engineNo");
        String citySelected = (String) context.get("cityCode");
        String sessionId = (String) context.get("sessionId");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("engineNo", engineNo);
        requestParams.put("citySelected", citySelected);
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

