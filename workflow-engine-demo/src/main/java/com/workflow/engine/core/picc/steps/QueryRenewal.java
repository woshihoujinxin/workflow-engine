package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.picc.util.PiccBizUtil.handleResponse;
import static com.workflow.engine.core.picc.util.PiccBizUtil.sendPostRequest;

/**
 * 续保信息查询
 * Created by houjinxin on 16/3/9.
 */
public class QueryRenewal implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(QueryRenewal.class);

    private static final String _API_QUERY_RENEWAL = "http://www.epicc.com.cn/wap/carProposal/renewal/queryRenewal";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        Map<String, Object> responseMap = handleResponse(sendPostRequest(requestParams, _API_QUERY_RENEWAL));
        String response = (String) responseMap.get("response");
        if ((boolean) responseMap.get("isJson")) {
            JsonNode jsonNode = JacksonUtil.getJsonNode(response);
            String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
            if ("1".equals(resultCode)) {
                Map<String, Object> vehicleInfo = new HashMap<>();
                Map<String, String> appliCarInfo = JacksonUtil.getMapNodeByKey(jsonNode, "appliCarInfo");
                Map<String, String> appliInfo = JacksonUtil.getMapNodeByKey(jsonNode, "appliInfo");
                Map<String, String> insuredInfo = JacksonUtil.getMapNodeByKey(jsonNode, "insuredInfo");
                vehicleInfo.put("appliCarInfo", appliCarInfo);
                vehicleInfo.put("appliInfo", appliInfo);
                vehicleInfo.put("insuredInfo", insuredInfo);
                context.put("vehicleInfo", vehicleInfo);
                context.put("reuseCarData", true);
                logger.info("获取续保信息成功,车型信息可复用");
                return BusinessUtil.successfulStepState(this);
            } else {
                logger.info("续保信息查询失败,响应为:\n{}", response);
                return BusinessUtil.failureStepState(this);
            }
        } else {
            if ("2".equals(response) || "3".equals(response) || "4".equals(response)) {
                logger.info("证件号或上年保单号不匹配");
                return BusinessUtil.failureStepState(this, MsgEnum.WrongIdentity);
            } else if ("7".equals(response)) {
                logger.info("暂不支持网上投保!", response);
                return BusinessUtil.failureStepState(this, MsgEnum.CannotQuoteOnLine);
            } else {
                logger.info("未知原因出错,联系客服人工报价!,响应为:\n{}", response);
                return BusinessUtil.failureStepState(this, MsgEnum.Other);
            }
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String beforeProposalNo = (String) context.get("identity");//去年投保人身份证
        String licenseNo = (String) context.get("licenseNo");
        String sessionId = (String) context.get("sessionId");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("beforeProposalNo", beforeProposalNo);
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

