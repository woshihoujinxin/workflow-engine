package com.workflow.engine.core.picc.steps;


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
 * 车辆数据可复用检查
 * Created by houjinxin on 16/3/9.
 */
public class CarDataReuseCheck implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CarDataReuseCheck.class);

    private static final String _API_CAR_DATA_REUSE = "http://www.epicc.com.cn/wap/carProposal/car/carDataReuse";
    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_CAR_DATA_REUSE, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(response);
        String status = getStringNodeByKey(jsonNode, "status");
        if("success".equals(status)){
            String resultCode = getStringNodeByKey(jsonNode, "resultCode");
            logger.info("车辆数据{}", "1".equals(resultCode) ? "可复用" : "不可复用" );
            if ("1".equals(resultCode)){
                context.put("reuseCarData", true); //reuseCarData 用于决定车型的来源 若车型数据可复用直接复用,若不可复用选择直接查询
                return successfulStepState(this, "车辆数据可复用");
            } else{ //resultCode=0
                context.put("reuseCarData", false);
                return successfulStepState(this, "车辆数据不可复用");
            }
        } else {
            return failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String licenseNo = (String) context.get("licenseNo");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("licenseflag", "1");
        requestParams.put("licenseNo", licenseNo);
        return requestParams;
    }
}

