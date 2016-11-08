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
 * 校验车型
 * Created by houjinxin on 16/3/9.
 */
public class CarChecked implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CarChecked.class);

    private static final String _API_CAR_CHECKED = "http://www.epicc.com.cn/wap/carProposal/carSelect/carChecked";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_CAR_CHECKED, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if("1".equals(resultCode)){
            logger.info("校验车辆成功,结果为:\n{}", response);
            return successfulStepState(this);
        } else {
            logger.info("校验车辆失败,结果为:\n{}", response);
            return failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");

        String modelCode;
        String seatCount;
        String parentId;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        boolean reuseCarData = (boolean) context.get("reuseCarData");
        boolean cityIsBeijing = (boolean) context.get("cityIsBeijing");
        if (reuseCarData && cityIsBeijing) {
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            modelCode = appliCarInfo.get("brandName");
            seatCount = appliCarInfo.get("seat");
            parentId = "0";
        } else {
            modelCode = (String) vehicleInfo.get("modelCode");
            seatCount = (String) vehicleInfo.get("seat");
            parentId = (String) vehicleInfo.get("parentId");
        }
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);  //上年投保省
        requestParams.put("parentId", parentId); //上年投保市
        requestParams.put("sessionId", sessionId);
        requestParams.put("modelCode", modelCode);
        requestParams.put("countryNature", "");
        requestParams.put("seatCount", seatCount);
        return requestParams;
    }
}

