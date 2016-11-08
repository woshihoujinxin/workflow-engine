package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.utils.JsonHelper;
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
 * 起保日期校验
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class CheckStartDate implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CheckStartDate.class);

    private static final String _API_CHECK_START_DATE = "http://www.epicc.com.cn/wap/carProposal/checkStartDate/checkStartDate";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_CHECK_START_DATE, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if("1".equals(resultCode)){
            logger.info("起保日期校验成功,结果为:\n{}", response);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("起保日期校验成功,结果为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String licenseNo = (String) context.get("licenseNo");
        String enrollDate = (String) context.get("enrollDate");
        String isRenewal = (String) context.get("isRenewal");
        String startDate = (String) context.get("startDate");
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);  //上年投保省
        requestParams.put("citySelected", citySelected); //上年投保市
        requestParams.put("ccaFlag", "");
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("enrollDate", enrollDate);
        requestParams.put("startdate", startDate);
        requestParams.put("isRenewal", isRenewal);
        requestParams.put("licenseFlag", "1");
        requestParams.put("lastDamagedBI", "");
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

