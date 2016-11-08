package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.checkCompulsoryIsChoosed;
import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.getBirthdayFromIdentity;
import static com.workflow.engine.core.common.utils.BusinessUtil.getGenderFromIdentity;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;

/**
 * 交强险报价
 * Created by houjinxin on 16/3/9.
 */
public class CalculateFeeJQ implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CalculateFeeJQ.class);

    private static final String _API_CALCULATE_FEE_JQ = "http://www.epicc.com.cn/wap/carProposal/calculateFee/jq";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        if(!checkCompulsoryIsChoosed((InsurancePackageVO) context.get("insurancePackage"))) {
            logger.info("套餐中不包含交强险,直接跳过此步骤!");
            return successfulStepState(this);
        } else {
            Map headers = PiccBizUtil.getHeaders();
            headers.put("Referer", "http://www.epicc.com.cn/wap/carProposal/car/calculateFee");
            Map<String, String> requestParams = getRequestParams(context);
            String response = executePostMethod(_API_CALCULATE_FEE_JQ, requestParams, headers);
            JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
            if(jsonNode.findValue("status") != null && "success".equals(getStringNodeByKey(jsonNode, "status"))){
                context.put("forceCanApply", false); //交强险不可投保
                MsgEnum msgEnum = MsgEnum.ForceFailure;
                String message = getStringNodeByKey(jsonNode, "message");
                String resultCode = message.substring(0,1);
                String msg = message.substring(1, message.length());
                if ("2".equals(resultCode) && msg.indexOf("连接平台失败：该车已经在本公司投保了同类型的险种") > -1){
                    String errorMsg = "交强险已投保,保险期限是" + message.substring(message.indexOf("[") ,  message.indexOf("]") + 1);
                    msgEnum.setMsg(errorMsg);
                    logger.info(errorMsg);
                } else if("6".equals(resultCode) && msg.indexOf("您的车辆上年交强险终保日期为") > -1) {
                    String earliestForceBeginDate = msg.substring(msg.indexOf("。") + 1, msg.length()).replaceAll("/", "-");
                    context.put("earliestForceBeginDate", earliestForceBeginDate);
                    msgEnum.setMsg("交强险未到投保日期, 最早投保日期为" + earliestForceBeginDate);
                    logger.info("交强险最早投保日期为:{}", earliestForceBeginDate);
                }
                context.put("forceFailure", msgEnum);
                return successfulStepState(this);
            } else if(jsonNode.findValue("premiumBZ").asDouble() != 0.0){
                Map<String, String> forceInfo = JacksonUtil.getMapNodeByKey(jsonNode, "");
                String compulsoryPremium = forceInfo.get("premiumBZ");
                double prePayTax = Double.parseDouble(forceInfo.get("prePayTax"));
                double thisPayTax = Double.parseDouble(forceInfo.get("thisPayTax"));
                double delayPayTax = Double.parseDouble(forceInfo.get("delayPayTax"));
                String autoTaxPremium = String.valueOf(prePayTax + thisPayTax + delayPayTax);
                //交强险和车船税报价结果放到上线文中
                context.put("compulsoryPremium", compulsoryPremium);
                context.put("autoTaxPremium", autoTaxPremium);
                logger.info("交强险报价成功, 交强险价格为:{}, 车船税价格为", compulsoryPremium, autoTaxPremium);

                //TODO: 关于起保日期的获取方式还要经过测试检验,以下做法不是正确做法
                Date payEndDate;
                if(forceInfo.get("payEndDate") != null) {
                    payEndDate = DateUtils.parse(forceInfo.get("payEndDate"), "yyyy/MM/dd");
                } else {
                    payEndDate = new Date();
                }
                String forceBeginDate = DateUtils.format(DateUtils.addDay(payEndDate, 1), "yyyy-MM-dd");
                context.put("forceBeginDate", forceBeginDate);
                logger.info("交强险起保日期为{}", forceBeginDate);
                return successfulStepState(this);
            } else {

                logger.info("交强险报价失败,响应为: \n{}", response);
                return failureStepState(this);
            }
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) throws ParseException {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String carOwnerIdentifyType = "01"; //身份证类型
        String carOwnerIdentifyNumber = (String) context.get("identity");
        String licenseNo = (String) context.get("licenseNo");
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        String enrollDate = (String) context.get("enrollDate");
        String insuredName = (String) context.get("carOwner");
        String engineNo = (String) context.get("engineNo");

        String seatCount;
        String appliEmail;
        String appliMobile;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        if ((boolean) context.get("reuseCarData")) {
            Map<String, String> appliInfo = (Map<String, String>) vehicleInfo.get("appliInfo");
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            seatCount = appliCarInfo.get("seat");
            appliEmail = appliInfo.get("appliEmail");
            appliMobile =  appliInfo.get("appliMobile");
        } else {
            seatCount = (String) vehicleInfo.get("seat");
            appliEmail = "";
            appliMobile = "";
        }

        String owner = (String) context.get("carOwner");
        String isRenewal = (String) context.get("isRenewal"); // 0 转保, 1 续保, 2 新车未上牌
        String birthday = getBirthdayFromIdentity(carOwnerIdentifyNumber, "yyyy/MM/dd");
        String sex = getGenderFromIdentity(carOwnerIdentifyNumber, "1" , "2");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("sessionId", sessionId);
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected",citySelected);
        requestParams.put("carOwnerIdentifytype", carOwnerIdentifyType); //证件类型
        requestParams.put("carOwnerIdentifynumber", carOwnerIdentifyNumber); //证件ID
        requestParams.put("areaCodeLast", "");
        requestParams.put("cityCodeLast", "");
        requestParams.put("mobile", appliMobile);
        requestParams.put("email", appliEmail);
        requestParams.put("identifytype", "");
        requestParams.put("identifynumber", "");
        requestParams.put("birthday", birthday);
        requestParams.put("sex", sex);
        requestParams.put("startdate", startDate); // 当天的下一天
        requestParams.put("starthour", "0");
        requestParams.put("enddate", endDate);
        requestParams.put("endhour", "24");
        requestParams.put("isRenewal", isRenewal); //新车未上牌有关 默认是0 2为新车未上牌
        requestParams.put("licenseno", licenseNo);
        requestParams.put("nonlocalflag", "0");
        requestParams.put("licenseflag", "1");  //车型标志  与是否新车未上牌有关 如果是老车为 1 新车未0
        requestParams.put("engineno", engineNo);
        requestParams.put("frameno", frameNo);
        requestParams.put("vinno", vinNo);
        requestParams.put("newcarflag", "0");
        requestParams.put("isOutRenewal", "0");
        requestParams.put("lastHas050200", "0");
        requestParams.put("lastHas050210", "0");
        requestParams.put("lastHas050500", "0");
        requestParams.put("enrolldate", enrollDate);
        requestParams.put("transfervehicleflag", "");
        requestParams.put("insuredname", insuredName);
        requestParams.put("fullAmountName", "");
        requestParams.put("beforeProposalNo", "");
        requestParams.put("startDateCI", startDate);
        requestParams.put("starthourCI", "0");
        requestParams.put("endDateCI", endDate);
        requestParams.put("endhourCI", "24");
        requestParams.put("taxpayeridentno", "");
        requestParams.put("taxpayername", owner);
        requestParams.put("taxtype", "");
        requestParams.put("certificatedate", "");
        requestParams.put("transferdate", "");
        requestParams.put("runAreaCodeName", "");
        requestParams.put("assignDriver", "2"); //是否指定驾驶员  1指定 2不指定
        requestParams.put("haveLoan", "2"); //是否贷款车 1是  2 不是
        requestParams.put("LoanName", "");
        requestParams.put("weiFaName", "");
        requestParams.put("seatCount", seatCount);
        requestParams.put("transferdate", "");
        requestParams.put("seatflag", "");
        requestParams.put("carDrivers", "");
        requestParams.put("ccaFlag", "");
        requestParams.put("ccaID", "");
        requestParams.put("ccaEntryId", "");
        requestParams.put("travelMilesvalue", "");
        requestParams.put("isbuytax", "");
        return requestParams;
    }
}

