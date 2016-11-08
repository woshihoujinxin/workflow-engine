package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 核保
 * Created by houjinxin on 16/3/9.
 */
public class UnderwriteCheckProfitAjax implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(UnderwriteCheckProfitAjax.class);

    private static final String _API_UNDER_WRITE_CHECK_PROFIT_AJAX = "http://www.epicc.com.cn/wap/carProposal/underWrite/underwriteCheckProfitAjax";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_UNDER_WRITE_CHECK_PROFIT_AJAX, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("核保成功,响应为:\n{}", response);
            return BusinessUtil.successfulStepState(this);
        } else if("2".equals(resultCode) && "请稍候重试".equals(JacksonUtil.getStringNodeByKey(jsonNode, "resultMsg"))){
            logger.info("该车暂不支持网上投保,请联系客服!");
            return BusinessUtil.failureStepState(this, MsgEnum.CannotQuoteOnLine);
        } else if("4".equals(resultCode) && "温馨提示：网销产品只能适用家庭自用客车".equals(JacksonUtil.getStringNodeByKey(jsonNode, "resultMsg"))) {
            logger.info("网销产品只能适用家庭自用客车!");
            return BusinessUtil.failureStepState(this, MsgEnum.NonHomeCar);
        } else {
            logger.info("核保失败,响应为: \n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String lastCarOwnerName = (String) context.get("carOwner");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String licenseNo = (String) context.get("licenseNo");
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        String enrollDate = (String) context.get("enrollDate");
        String engineNo = (String) context.get("engineNo");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        String carOwner = (String) context.get("carOwner");
        String isRenewal = (String) context.get("isRenewal");
        String interimNo = (String) context.get("interimNo");


        String seatCount;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        boolean reuseCarData = (boolean) context.get("reuseCarData");
//        boolean cityIsBeijing = (boolean) context.get("cityIsBeijing");÷
        if (reuseCarData) {
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            seatCount = appliCarInfo.get("seat");
        } else {
            seatCount = (String) vehicleInfo.get("seat");
        }

        Map<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("lastcarownername", lastCarOwnerName);
        requestParams.put("channelNo", "2");
        requestParams.put("areaCodeLast", ""); //上年投保省
        requestParams.put("cityCodeLast", ""); //上年投保市
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("startdate", startDate);
        requestParams.put("starthour", "0");
        requestParams.put("enddate", endDate);
        requestParams.put("endhour", "24");
        requestParams.put("licenseno", licenseNo);
        requestParams.put("engineno", engineNo);
        requestParams.put("vinno", vinNo);
        requestParams.put("frameno", frameNo);
        requestParams.put("seatcount", seatCount);
        requestParams.put("carOwner", carOwner);
        requestParams.put("isRenewal", isRenewal);
        requestParams.put("enrolldate", enrollDate);
        requestParams.put("guohuselect", "");
        requestParams.put("licenseflag", "1");
        requestParams.put("isOutRenewal", "0");
        requestParams.put("lastHas050200", "0");
        requestParams.put("lastHas050210", "0");
        requestParams.put("lastHas050500", "0");
        requestParams.put("seatflag", "");
        requestParams.put("transferdate", "");
        requestParams.put("sessionId", sessionId);
        requestParams.put("ccaID", "");
        requestParams.put("ccaEntryId", "");
        requestParams.put("ccaFlag", "");
        requestParams.put("lastdamagedbi", "");
        requestParams.put("guohuflag", "0");
        requestParams.put("runAreaCodeName", "");
        requestParams.put("assignDriver", "2");
        requestParams.put("haveLoan", "2");
        requestParams.put("LoanName", "");
        requestParams.put("weiFaName", "");
        requestParams.put("carDrivers", "");
        requestParams.put("oldPolicyNo", "");
        requestParams.put("interimNo", interimNo);
        return requestParams;
    }
}

