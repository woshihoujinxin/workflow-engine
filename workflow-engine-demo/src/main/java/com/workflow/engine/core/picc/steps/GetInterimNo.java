package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 转保获取interimNo
 * Created by houjinxin on 16/3/9.
 */
public class GetInterimNo implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(GetInterimNo.class);

    private static final String _API_INTERIM = "http://www.epicc.com.cn/wap/carProposal/car/interim";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_INTERIM, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            String interimNo = JacksonUtil.getStringNodeByKey(jsonNode, "interimNo");
            logger.info("interimNo获取成功,{}", interimNo);
            context.put("interimNo", interimNo);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("interimNo获取失败,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) throws ParseException {
        String licenseNo = (String) context.get("licenseNo");
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String lastCarOwnerName = (String) context.get("carOwner");
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        String engineNo = (String) context.get("engineNo");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        String enrollDate = (String) context.get("enrollDate");
        String identity = (String) context.get("identity");
        String isRenewal = (String) context.get("isRenewal");
        String carOwner = (String) context.get("carOwner");

        String standardName;
        String seatCount;
        String appliEmail;
        String appliMobile;
        String insuredEmail;
        String insuredMobile;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        if ((boolean) context.get("reuseCarData")) {
            Map<String, String> appliInfo = (Map<String, String>) vehicleInfo.get("appliInfo");
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            Map<String, String> insuredInfo = (Map<String, String>) vehicleInfo.get("insuredInfo");
            standardName = appliCarInfo.get("standardName"); //上游步骤设置
            seatCount = appliCarInfo.get("seat");
            appliEmail = appliInfo.get("appliEmail");
            appliMobile = appliInfo.get("appliMobile");
            insuredEmail = insuredInfo.get("insuredEmail");
            insuredMobile = insuredInfo.get("insuredMobile");
        } else {
            standardName = (String) vehicleInfo.get("vehicleFgwCode");
            seatCount = (String) vehicleInfo.get("seat");
            appliEmail = "";
            appliMobile = "";
            insuredEmail = "";
            insuredMobile = "";
        }

        Map<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("mobileflag", "1");
        requestParams.put("licenseno", licenseNo);
        requestParams.put("sessionId", sessionId);
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("areaCodeLast", proSelected);
        requestParams.put("cityCodeLast", citySelected);
        requestParams.put("insuredIdentifSex", "");
        requestParams.put("insuredBirthday", BusinessUtil.getBirthdayFromIdentity(identity, "yyyy/MM/dd"));
        requestParams.put("lastcarownername", lastCarOwnerName);
        requestParams.put("startdate", startDate);
        requestParams.put("enddate", endDate);
        requestParams.put("endhour", "24");
        requestParams.put("startDateCI", "");
        requestParams.put("startHourCI", "");
        requestParams.put("endDateCI", "");
        requestParams.put("endHourCI", "");
        requestParams.put("engineno", engineNo);
        requestParams.put("vinno", vinNo);
        requestParams.put("frameno", frameNo);
        requestParams.put("enrolldate", enrollDate);
        requestParams.put("standardName", standardName);
        requestParams.put("seatcount", seatCount);
        requestParams.put("insuredEmail", insuredEmail);
        requestParams.put("insuredMobile", insuredMobile);
        requestParams.put("appliEmail", appliEmail);
        requestParams.put("appliMobile", appliMobile);
        requestParams.put("linkAddress", "");
        requestParams.put("runAreaCodeName", "");
        requestParams.put("assignDriver", "2");
        requestParams.put("carDrivers", "");
        requestParams.put("haveLoan", "2");
        requestParams.put("LoanName", "");
        requestParams.put("guohuselect", "");
        requestParams.put("transferdate", "");
        requestParams.put("fullAmountName", "");
        requestParams.put("appliIdentifyNumber", identity);
        requestParams.put("appliIdentifyType", "01");
        requestParams.put("appliName", carOwner);
        requestParams.put("taxPayerIdentNo", "");
        requestParams.put("taxPayerName", "");
        requestParams.put("aliasName", "");
        requestParams.put("carOwerIdentifyType", "01");
        requestParams.put("carOwner", carOwner);
        requestParams.put("insuredIdentifyAddr", "");
        requestParams.put("insuredIdentifyNumber", identity);
        requestParams.put("argueSolution", "");
        requestParams.put("insuredAndOwnerrelate", "");
        requestParams.put("arbitboardname", "");
        requestParams.put("appliAddName", "");
        requestParams.put("deliverInfoPro", "");
        requestParams.put("deliverInfoCity", "");
        requestParams.put("deliverInfoDistrict", "");
        requestParams.put("appliPhoneNumber", "");
        requestParams.put("invoiceTitle", "");
        requestParams.put("itemKindFlag", "2");
        requestParams.put("licenseflag", "1");
        requestParams.put("certificatedate", "");
        requestParams.put("monopolyname", "");
        requestParams.put("weiFaName", "");
        requestParams.put("isRenewal", isRenewal);
        requestParams.put("interimNo", "");
        requestParams.put("beforeProposalNo", "");
        requestParams.put("taxPayerIdentType", "");
        requestParams.put("carKindCI", "");
        requestParams.put("bjfuel_type", "");
        requestParams.put("certificate_type", "");
        requestParams.put("certificate_no", "");
        requestParams.put("certificate_date", "");
        requestParams.put("carIdentifyAddressSX", "");
        requestParams.put("carNameSX", "");
        requestParams.put("carKindSX", "");
        requestParams.put("ccaId", "");
        return requestParams;
    }
}

