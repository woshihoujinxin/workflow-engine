package com.workflow.engine.core.pingan.steps;


import com.workflow.engine.core.common.utils.HttpHelper;
import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.utils.MapUtil;
import com.workflow.engine.core.service.entity.vo.AreaVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.pingan.utils.PinganBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0000;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0001;

/**
 * 续保检查以及获取FlowId
 * Created by houjinxin on 16/3/9.
 */
public class RenewalCheckAndGetFlowId implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(RenewalCheckAndGetFlowId.class);

    private static final String _API_RENEWAL_CHECK = "http://u.pingan.com/autox/do/api/renewal-check?";

    @Override
    public StepState run(Map<String, Object> context) throws IOException {
        Map<String, String> requestParams = getRequestParams(context);
        Map<String, Object> responseMap = handleResponse(sendGetRequest(_API_RENEWAL_CHECK, requestParams));
        if (responseMap.get("xrc") == null) {
            logger.info("获取xrc失败,流程终止");
            return BusinessUtil.failureStepState(this);
        }
        JsonNode jsonNode = JacksonUtil.getJsonNode((String) responseMap.get("response"));
        String flowId = JacksonUtil.getStringNodeByKey(jsonNode, "flowId");
        if (flowId != null) { //获取到FlowId
            context.put("flowId", flowId);
            String xrc = (String) responseMap.get("xrc");
            context.put("xrc", xrc);
            String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
            logger.info("获取flowId:{}, xrc:{}", flowId, xrc);
            if (resultCode.equals(_RESULT_CODE_C0000)) {
                context.put("isRenewal", false);
                logger.info("该车是转保车辆");
                return BusinessUtil.successfulStepState(this);
            } else if (resultCode.equals(_RESULT_CODE_C0001)) {
                context.put("isRenewal", true);
                logger.info("该车是续保车辆");
                return BusinessUtil.successfulStepState(this);
            } else {
                logger.info("未知的车辆类型, 响应为:{}", responseMap.get("response"));
                return BusinessUtil.failureStepState(this);
            }
        } else {
            logger.info("未能获取FlowId, 响应为:{}", responseMap.get("response"));
            return BusinessUtil.failureStepState(this);
        }
    }

    private Map<String, String> getRequestParams(Map<String, Object> context) {
        //获取保险公司区域相关代码,一般在流程的第一步中设置
        Map<String, String> cityCodeMappings = (Map<String, String>) context.get("cityCodeMapping");
        AreaVO area = (AreaVO) context.get("area");
        context.put("cityCode", area.getCityCode()); //保险公司CityCode
        context.put("provinceCode", cityCodeMappings.get("cityCode"));//保险公司provinceCode

        String cityCode = (String) context.get("cityCode");
        String licenseNo = (String) context.get("licenseNo");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("department.cityCode", cityCode);
        requestParams.put("vehicle.licenseNo", licenseNo);
        requestParams.put("partner.mediaSources", "sc03-direct-mpingan");
        requestParams.put("partner.partnerName", "chexian-mobile");
        return requestParams;
    }

    private HttpResponse sendGetRequest(String requestUrl, Map<String, String> requestParams) throws IOException {
        HttpGet get = new HttpGet(requestUrl + MapUtil.mapJoinIncludeValueNull(requestParams, false, true));
        HttpHelper.setHeaders(get, PinganBizUtil.getHeaders());
        return HttpHelper.exec(HttpHelper.getHttpClient(), get);
    }

    private Map<String, Object> handleResponse(HttpResponse response) throws IOException {
        Header[] responseHeaders = response.getAllHeaders();
        String responseData;
        String xrc = null;
        Map<String, Object> responseMap = new HashMap<String, Object>();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            responseData = JsonHelper.convert(EntityUtils.toString(response.getEntity(), "utf-8"));
            for (Header header : responseHeaders) {
                if (header.getName().trim().equals("__xrc")) {
                    xrc = header.getValue();
                }
            }
        } else {
            xrc = null;
            responseData = null;
        }
        responseMap.put("xrc", xrc);
        responseMap.put("response", responseData);
        return responseMap;
    }

}
