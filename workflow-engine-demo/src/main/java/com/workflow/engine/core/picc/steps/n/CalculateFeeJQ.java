package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 交强险报价
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""}),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "carOwnerIdentifytype", strategy = StrategyType.FIXED, strategyNeedParams = "01"),
        @PGScheme(requestParamName = "carOwnerIdentifynumber", strategy = StrategyType.CONTEXT, strategyNeedParams = {"identity", ""}),
        @PGScheme(requestParamName = "areaCodeLast", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "cityCodeLast", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "mobile", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.appliEmail", "18911111111"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"", "18911111111"}),
        @PGScheme(requestParamName = "email", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.appliMobile", "121@qq.com"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"", "121@qq.com"}),
        @PGScheme(requestParamName = "identifytype", strategy = StrategyType.FIXED, strategyNeedParams = "01"),
        @PGScheme(requestParamName = "identifynumber", strategy = StrategyType.CONTEXT, strategyNeedParams = {"identity", ""}),
        @PGScheme(requestParamName = "birthday", strategy = StrategyType.METHOD, strategyNeedParams = "$getBirthdayFromIdentity(identity, \"yyyy/MM/dd\")"),
        @PGScheme(requestParamName = "sex", strategy = StrategyType.METHOD, strategyNeedParams = "$getGenderFromIdentity(identity,\"1\",\"2\")"),
        @PGScheme(requestParamName = "startdate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"startDate", ""}),
        @PGScheme(requestParamName = "starthour", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "enddate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"endDate", ""}),
        @PGScheme(requestParamName = "endhour", strategy = StrategyType.FIXED, strategyNeedParams = "24"),
        @PGScheme(requestParamName = "isRenewal", strategy = StrategyType.CONTEXT, strategyNeedParams = {"isRenewal", ""}),
        @PGScheme(requestParamName = "licenseno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"licenseNo", ""}),
        @PGScheme(requestParamName = "nonlocalflag", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "licenseflag", strategy = StrategyType.FIXED, strategyNeedParams = "1"),
        @PGScheme(requestParamName = "engineno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"engineNo", ""}),
        @PGScheme(requestParamName = "vinno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "frameno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "newcarflag", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "isOutRenewal", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050200", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050210", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050500", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "enrolldate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"enrollDate", ""}),
        @PGScheme(requestParamName = "transfervehicleflag", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "insuredname", strategy = StrategyType.CONTEXT, strategyNeedParams = {"carOwner", ""}),
        @PGScheme(requestParamName = "fullAmountName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "beforeProposalNo", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "startDateCI", strategy = StrategyType.CONTEXT, strategyNeedParams = {"startDate", ""}),
        @PGScheme(requestParamName = "starthourCI", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "endDateCI", strategy = StrategyType.CONTEXT, strategyNeedParams = {"endDate", ""}),
        @PGScheme(requestParamName = "endhourCI", strategy = StrategyType.FIXED, strategyNeedParams = "24"),
        @PGScheme(requestParamName = "taxpayeridentno", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "taxpayername", strategy = StrategyType.CONTEXT, strategyNeedParams = {"carOwner", ""}),
        @PGScheme(requestParamName = "taxtype", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "certificatedate", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "transferdate", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "runAreaCodeName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "assignDriver", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "haveLoan", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "LoanName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "weiFaName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "seatCount", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.seat", "5"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"vehicleInfo.seat", "5"}),
        @PGScheme(requestParamName = "seatflag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "carDrivers", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaFlag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaID", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaEntryId", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "travelMilesvalue", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "isbuytax", strategy = StrategyType.FIXED, strategyNeedParams = "")
})
public class CalculateFeeJQ extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(CalculateFeeJQ.class);

    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = super.getHeaders();
        headers.put("Referer", "http://www.epicc.com.cn/wap/carProposal/car/calculateFee");
        return headers;
    }

    @Override
    protected boolean skipCurrentStepOrNot(Map<String, Object> context) {
        return !BusinessUtil.checkCompulsoryIsChoosed((InsurancePackageVO) context.get("insurancePackage"));
    }

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/calculateFee/jq";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(realResponse));
        if (jsonNode.findValue("status") != null && "success".equals(JacksonUtil.getStringNodeByKey(jsonNode, "status"))) {
            context.put("forceCanApply", false); //交强险不可投保
            MsgEnum msgEnum = MsgEnum.ForceFailure;
            String message = JacksonUtil.getStringNodeByKey(jsonNode, "message");
            String resultCode = message.substring(0, 1);
            String msg = message.substring(1, message.length());
            if ("2".equals(resultCode) && msg.contains("连接平台失败：该车已经在本公司投保了同类型的险种")) {
                String errorMsg = "交强险已在人保投保,保险期限是" + message.substring(message.indexOf("["), message.indexOf("]") + 1);
                msgEnum.setMsg(errorMsg);
                logger.info(errorMsg);
            } else if ("4".equals(resultCode) && msg.contains("连接平台失败：该车已经在其他公司投保了同类型的险种")) {
                String errorMsg = "交强险已在其他保险公司投保,保险期限是" + message.substring(message.indexOf("["), message.indexOf("]") + 1);
                msgEnum.setMsg(errorMsg);
                logger.info(errorMsg);
            } else if ("6".equals(resultCode) && msg.contains("您的车辆上年交强险终保日期为")) {
                String earliestForceBeginDate = msg.substring(msg.indexOf("。") + 1, msg.length()).replaceAll("/", "-");
                context.put("earliestForceBeginDate", earliestForceBeginDate);
                msgEnum.setMsg("交强险未到投保日期, 最早投保日期为" + earliestForceBeginDate);
                logger.info("交强险最早投保日期为:{}", earliestForceBeginDate);
            } else if ("8".equals(resultCode) && msg.contains("您来早了现在还不能投保交强险")) {
                msgEnum.setMsg(msg);
                logger.info(msg);
            } else {
                String errorMsg = "未知原因导致交强险投保失败";
                msgEnum.setMsg(errorMsg);
                logger.info("交强险报价失败原因:" + message);
            }
            context.put("forceFailure", msgEnum);
            return BusinessUtil.successfulStepState(this);
        } else if (jsonNode.findValue("premiumBZ").asDouble() != 0.0) {
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
            if (forceInfo.get("payEndDate") != null) {
                payEndDate = DateUtils.parse(forceInfo.get("payEndDate"), "yyyy/MM/dd");
            } else {
                payEndDate = new Date();
            }
            String forceBeginDate = DateUtils.format(DateUtils.addDay(payEndDate, 1), "yyyy-MM-dd");
            context.put("forceBeginDate", forceBeginDate);
            logger.info("交强险起保日期为{}", forceBeginDate);
            return BusinessUtil.successfulStepState(this);
        } else {

            logger.info("交强险报价失败,响应为: \n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

