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
 * 车架号校验
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class RandFrameNo implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(RandFrameNo.class);

    private static final String _API_RAND_FRAME_NO = "http://www.epicc.com.cn/wap/carProposal/car/randFrameNo";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_RAND_FRAME_NO, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("随机车架号成功:\n{}", response);
            return successfulStepState(this);
        } else {
            logger.info("随机车架号失败,响应为:\n{}", response);
            return failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String frameNo = (String) context.get("frameNo");
        String enrollDate = (String) context.get("enrollDate");
        String isRenewal = (String) context.get("isRenewal"); // 0 转保, 1 续保, 2 新车未上牌

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("FrameNo", frameNo);
        requestParams.put("EnrollDate", enrollDate);
        requestParams.put("isRenewal", isRenewal); //新车未上牌有关 默认是0 2为新车未上牌
        requestParams.put("isFocus", "");
        return requestParams;
    }
}

