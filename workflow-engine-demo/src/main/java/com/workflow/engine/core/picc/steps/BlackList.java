package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
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
 * 黑名单
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class BlackList implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(BlackList.class);

    private static final String _API_BLACKLIST = "http://www.epicc.com.cn/wap/carProposal/underWrite/blackList";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_BLACKLIST, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if("1".equals(resultCode)){
            logger.info("不在黑名单中,响应为:\n{}", response);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("在黑名单中,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }


    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String licenseNo = (String) context.get("licenseNo");
//        String carOwner = (String) context.get("carOwner");
        String engineNo = (String) context.get("engineNo");
        String frameNo = (String) context.get("frameNo");
        String vinNo = (String) context.get("frameNo");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
//        requestParams.put("carOwner", carOwner);
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("engineNo", engineNo);
        requestParams.put("frameNo", frameNo);
        requestParams.put("vinNo", vinNo);
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

