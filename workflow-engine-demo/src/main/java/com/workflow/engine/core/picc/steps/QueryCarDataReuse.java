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

import static com.workflow.engine.core.common.MsgEnum.WrongIdentity;
import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getMapNodeByKey;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;

/**
 * 查询车辆数据可复用
 * Created by houjinxin on 16/3/9.
 */
public class QueryCarDataReuse implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(QueryCarDataReuse.class);

    private static final String _API_QUERY_CAR_DATA_REUSE = "http://www.epicc.com.cn/wap/carProposal/car/query_carDataReuse";
    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_QUERY_CAR_DATA_REUSE, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(response);
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if("1".equals(resultCode) || "5".equals(resultCode)){
            Map<String, String> vehicleInfo = getMapNodeByKey(jsonNode, "message");
            logger.info("车型信息:{}", vehicleInfo);
            context.put("vehicleInfo", vehicleInfo);
            //TODO:加入判断是否是新车的逻辑,现在默认都是新车
            if ((boolean)context.get("cityIsBeijing")) {
                logger.info("当前城市为北京, 北京的新车需要单独查询一次车型");
                context.put("reuseCarData", false);
                return successfulStepState(this, "北京市新车单独查询车型");
            }
            return successfulStepState(this);
        } else {
            logger.info("车型信息查询失败,原因为证件号码不匹配,响应为:{}", response);
            return failureStepState(this, WrongIdentity);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String licenseNo = (String) context.get("licenseNo");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String beforeProposalNo = (String) context.get("identity"); //身份证号

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("beforeProposalNo", beforeProposalNo);
        requestParams.put("licenseNo", licenseNo);
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

