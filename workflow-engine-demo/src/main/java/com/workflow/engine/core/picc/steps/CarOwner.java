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
 * 查询车主信息
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class CarOwner implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CarOwner.class);

    private static final String _API_CAR_OWNER = "http://www.epicc.com.cn/wap/carProposal/fastPrice/carOwner";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_CAR_OWNER, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("车主信息为:\n{}", response);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("车主信息查询失败,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String proSelected = (String) context.get("provinceCode");
        String carOwner = (String) context.get("carOwner");
        String isRenewal = (String) context.get("isRenewal"); // 0 转保, 1 续保, 2 新车未上牌

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("proSelected", proSelected);
        requestParams.put("carOwner", carOwner);
        requestParams.put("isRenewal", isRenewal); //新车未上牌有关 默认是0 2为新车未上牌
        requestParams.put("channelNo", "2");
        return requestParams;
    }
}

