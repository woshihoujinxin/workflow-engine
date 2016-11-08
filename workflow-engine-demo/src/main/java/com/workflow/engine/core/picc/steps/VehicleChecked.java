package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.picc.util.PiccBizUtil.handleResponse;
import static com.workflow.engine.core.picc.util.PiccBizUtil.sendPostRequest;

/**
 * 查询车型
 * Created by houjinxin on 16/3/9.
 */
public class VehicleChecked implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(VehicleChecked.class);

    private static final String _API_VEHICLE_CHECKED = "http://www.epicc.com.cn/wap/carProposal/carSelect/vehicleChecked";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        Map<String, Object> responseMap = handleResponse(sendPostRequest(requestParams, _API_VEHICLE_CHECKED));
        if ((boolean) responseMap.get("isJson")) {
            String response = (String) responseMap.get("response");
            JsonNode jsonNode = JacksonUtil.getJsonNode(response);
            String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
            if ("1".equals(resultCode)) {
                logger.info("车型校验通过");
//                context.put("countryNature", getStringNodeByKey(jsonNode, "countryNature"));
                context.put("seatFlag", "1".equals(JacksonUtil.getStringNodeByKey(jsonNode, "seatFlag")) ? "1" : "0");
                return BusinessUtil.successfulStepState(this);
            } else if ("4".equals(resultCode)) {
                logger.info("您所选取的车型有误，请仔细核对您的行驶证原件后重新选择");
                return BusinessUtil.failureStepState(this, MsgEnum.QueryCar_WrongVehicleInfo);
            } else {
                logger.info("车型校验失败,响应为:\n{}", response);
                return BusinessUtil.failureStepState(this);
            }
        } else {
            logger.info("车型不能通过校验,请修改品牌型号");
            return BusinessUtil.failureStepState(this, MsgEnum.QueryCar_WrongBrandCode);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String enrollDate = (String) context.get("enrollDate");
        String engineNo = (String) context.get("engineNo");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        //当queryCode是有效的值时才会返回车型列表,否则只会是乱码
        String carModel = (String) context.get("brandCode");
        Map<String, String> vehicleInfo = (Map<String, String>) context.get("vehicleInfo");
        String seatCount = vehicleInfo.get("seat");
        String queryCode = vehicleInfo.get("vehicleFgwCode");
        String parentId = vehicleInfo.get("parentId");

        Map<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("parentId", parentId);
        requestParams.put("queryCode", queryCode);
        requestParams.put("seatCount", seatCount);
        requestParams.put("carRequestType", "03");
        requestParams.put("licenseFlag", "1");
        requestParams.put("carModel", carModel);
        requestParams.put("sessionId", sessionId);
        requestParams.put("enrollDate", enrollDate);
        requestParams.put("frameNo", frameNo);
        requestParams.put("engineNo", engineNo);
        requestParams.put("vinNo", vinNo);
        return requestParams;
    }
}

