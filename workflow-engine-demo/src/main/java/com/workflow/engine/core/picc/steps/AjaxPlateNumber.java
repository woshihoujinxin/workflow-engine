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
 * Ajax校验车牌号
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class AjaxPlateNumber implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(AjaxPlateNumber.class);

    private static final String _API_AJAX_PLATE_NUMBER = "http://www.epicc.com.cn/wap/carProposal/car/ajaxPlateNumber";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_AJAX_PLATE_NUMBER, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String status = JacksonUtil.getStringNodeByKey(jsonNode, "status");
        if ("success".equals(status)) {
            logger.info("车牌号校验成功\n{}", response);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("车牌号校验失败,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String licenseNo = (String) context.get("licenseNo");
        String proSelected = (String) context.get("cityCode");
        String citySelected = (String) context.get("provinceCode");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("citySelected", citySelected);
        requestParams.put("proSelected", proSelected);
        requestParams.put("licenseflag", "1");
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("cooperE", "");
        requestParams.put("comName", "");
        return requestParams;
    }
}

