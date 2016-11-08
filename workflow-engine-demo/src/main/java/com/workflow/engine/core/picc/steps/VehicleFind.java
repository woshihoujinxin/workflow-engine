package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getListNodeByKey;
import static com.workflow.engine.core.picc.util.PiccBizUtil.handleResponse;
import static com.workflow.engine.core.picc.util.PiccBizUtil.sendPostRequest;

/**
 * 查询车型
 * Created by houjinxin on 16/3/9.
 */
public class VehicleFind implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(VehicleFind.class);

    private static final String _API_VEHICLE_FIND = "http://www.epicc.com.cn/wap/carProposal/carSelect/vehicleFind";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        Map<String, Object> responseMap = handleResponse(sendPostRequest(requestParams, _API_VEHICLE_FIND));
        if ((boolean) responseMap.get("isJson")) {
            String response = (String) responseMap.get("response");
            JsonNode jsonNode = JacksonUtil.getJsonNode(response);
            List<Map<String, String>> vehicles = getListNodeByKey(jsonNode, "");
            //TODO:车型查询完成后要做个判断,若果存在车型的唯一标识,那么就符合该标识的车型,否则将车型列表推送至前端
            logger.info("按照该品牌型号查询车型成功,选择列表中第一辆车");
            context.put("reuseCarData", false);
            Map<String, String> vehicleInfo = vehicles.get(0);
            context.put("vehicleInfo", vehicleInfo);
            logger.info("车型信息为:\n{}", vehicleInfo);
            return successfulStepState(this);
        } else {
            logger.info("品牌型号不正确,请重新输入");
            return failureStepState(this, MsgEnum.QueryCar_WrongBrandCode);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String licenseNo = (String) context.get("licenseNo");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String enrollDate = (String) context.get("enrollDate");
        String engineNo = (String) context.get("engineNo");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        //当queryCode是有效的值时才会返回车型列表,否则只会是乱码
        String queryCode = (String) context.get("brandCode");
        String isRenewal = (String) context.get("isRenewal");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("queryCode", queryCode);
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("frameNo", frameNo);
        requestParams.put("engineNo", engineNo);
        requestParams.put("enrollDate", enrollDate);
        requestParams.put("vinNo", vinNo);
        requestParams.put("isRenewal", isRenewal);
        requestParams.put("licenseFlag", "1");
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

