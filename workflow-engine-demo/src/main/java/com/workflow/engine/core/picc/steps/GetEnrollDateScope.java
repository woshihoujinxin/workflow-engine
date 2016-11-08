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
 * 获取初登日期范围
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class GetEnrollDateScope implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(GetEnrollDateScope.class);

    private static final String _API_GET_ENROLL_DATE_SCOPE = "http://www.epicc.com.cn/wap/carProposal/renewal/getEnrollDateScope";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_GET_ENROLL_DATE_SCOPE, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String status = getStringNodeByKey(jsonNode, "status");
        if("success".equals(status)){
            String enrollDate_MAX = getStringNodeByKey(jsonNode, "enrollDate_MAX");
            String enrollDate_MIN = getStringNodeByKey(jsonNode, "enrollDate_MIN");
            context.put("enrollDate_MAX", enrollDate_MAX);
            context.put("enrollDate_MIN", enrollDate_MIN);
            logger.info("初登日期范围获取成功,初登日期在{}-{}之间", enrollDate_MIN, enrollDate_MAX);
            return successfulStepState(this);
        } else {
            logger.info("校验初登日期失败,结果为:\n{}", response);
            return failureStepState(this);
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
        requestParams.put("cityCode", citySelected); //上年投保市
        requestParams.put("licenseflag", "1"); //是否上牌   1：未上牌  不是未上牌传空
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("quotepriceFlag", "0"); //快速报价标志  1：试算来源 0：非试算
        requestParams.put("enrollDate", enrollDate);
        requestParams.put("isRenewal", isRenewal);
        requestParams.put("startdate", startDate);
        requestParams.put("ccaFlag", "");
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

