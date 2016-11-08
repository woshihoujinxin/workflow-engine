package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 查询车辆信息
 * Created by houjinxin on 16/3/9.
 */
public class CarQuery implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CarQuery.class);

    private static final String _API_CAR_QUERY = "http://www.epicc.com.cn/wap/carProposal/carSelect/carQuery";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_CAR_QUERY, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            List<Map<String, String>> vehicleInfoList = JacksonUtil.getListNodeByKey(jsonNode, "queryVehicle");
            logger.info("车型查询成功,结果为:\n{}", vehicleInfoList);
            Map<String, String> vehicleInfo = vehicleInfoList.get(0); //获取第一辆车
            context.put("vehicleInfo", vehicleInfo);
            return BusinessUtil.successfulStepState(this);
        } else if("3".equals(resultCode)) {
            logger.info("没有对应车辆信息,请求确认输入信息是否正确");
            return BusinessUtil.failureStepState(this, MsgEnum.QueryCar);
        } else {
            logger.info("车型查询失败,结果为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String carOwner = (String) context.get("carOwner");
        String licenseNo = (String) context.get("licenseNo");
        String queryCode = (String) context.get("brandCode");
        String enrollDate = (String) context.get("enrollDate");
        String engineNo = (String) context.get("engineNo");
        String frameNo = (String) context.get("frameNo");
        String vinNo = (String) context.get("frameNo");
        String isRenewal = (String) context.get("isRenewal"); // 0 转保, 1 续保, 2 新车未上牌

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("carOwner", carOwner);
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("queryCode", queryCode);
        requestParams.put("engineNo", engineNo);
        requestParams.put("enrollDate", enrollDate);
        requestParams.put("frameNo", frameNo);
        requestParams.put("licenseFlag", "1");  //车型标志  与是否新车未上牌有关 如果是老车为 1 新车未0
        requestParams.put("vinNo", vinNo);
        requestParams.put("isRenewal", isRenewal); //新车未上牌有关 默认是0 2为新车未上牌
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

