package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 转保获取interimNo
 * Created by houjinxin on 16/3/9.
 */
public class GetInterimNo extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(GetInterimNo.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/car/interim";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("mobileflag")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("1"),
                ParamGeneratorUtil.schemeParam("licenseno")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("licenseNo", ""),
                ParamGeneratorUtil.schemeParam("sessionId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("sessionId", ""),
                ParamGeneratorUtil.schemeParam("proSelected")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.areaCode", "11000000"),
                ParamGeneratorUtil.schemeParam("citySelected")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.cityCode", "11000000"),
                ParamGeneratorUtil.schemeParam("areaCodeLast")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.areaCode", "11000000"),
                ParamGeneratorUtil.schemeParam("cityCodeLast")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.cityCode", "11000000"),
                ParamGeneratorUtil.schemeParam("insuredIdentifSex")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("insuredBirthday")
                        .chooseStrategy(StrategyType.METHOD)
                        .configStrategyNeedParams("$getBirthdayFromIdentity(identity, \"yyyy/MM/dd\")"),
                ParamGeneratorUtil.schemeParam("lastcarownername")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("carOwner", ""),
                ParamGeneratorUtil.schemeParam("startdate")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("startDate", ""),
                ParamGeneratorUtil.schemeParam("enddate")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("endDate", ""),
                ParamGeneratorUtil.schemeParam("endhour")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("24"),
                ParamGeneratorUtil.schemeParam("startDateCI")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("startHourCI")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("endDateCI")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("endHourCI")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("engineno")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("engineNo", ""),
                ParamGeneratorUtil.schemeParam("vinno")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("frameNo", ""),
                ParamGeneratorUtil.schemeParam("frameno")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("frameNo", ""),
                ParamGeneratorUtil.schemeParam("enrolldate")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("enrollDate", ""),
                ParamGeneratorUtil.schemeParam("standardName")
                        .valueSourceExpression("$val(context, \"reuseCarData\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.standardName", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.vehicleFgwCode", ""),
                ParamGeneratorUtil.schemeParam("seatcount")
                        .valueSourceExpression("$val(context, \"reuseCarData\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.seat", "5")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.seat", "5"),
                ParamGeneratorUtil.schemeParam("insuredEmail")
                        .valueSourceExpression("$val(context, \"reuseCarData\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.insuredEmail", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("", ""),
                ParamGeneratorUtil.schemeParam("insuredMobile")
                        .valueSourceExpression("$val(context, \"reuseCarData\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.insuredMobile", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("", ""),
                ParamGeneratorUtil.schemeParam("appliEmail")
                        .valueSourceExpression("$val(context, \"reuseCarData\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.appliEmail", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("", ""),
                ParamGeneratorUtil.schemeParam("appliMobile")
                        .valueSourceExpression("$val(context, \"reuseCarData\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.appliMobile", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("", ""),
                ParamGeneratorUtil.schemeParam("linkAddress")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("runAreaCodeName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("assignDriver")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("2"),
                ParamGeneratorUtil.schemeParam("carDrivers")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("haveLoan")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("2"),
                ParamGeneratorUtil.schemeParam("LoanName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("guohuselect")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("transferdate")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("fullAmountName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("appliIdentifyNumber")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("identity", ""),
                ParamGeneratorUtil.schemeParam("appliIdentifyType")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("01"),
                ParamGeneratorUtil.schemeParam("appliName")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("carOwner", ""),
                ParamGeneratorUtil.schemeParam("taxPayerIdentNo")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("taxPayerName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("aliasName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("carOwerIdentifyType")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("01"),
                ParamGeneratorUtil.schemeParam("carOwner")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("carOwner", ""),
                ParamGeneratorUtil.schemeParam("insuredIdentifyAddr")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("insuredIdentifyNumber")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("identity", ""),
                ParamGeneratorUtil.schemeParam("argueSolution")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("insuredAndOwnerrelate")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("arbitboardname")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("appliAddName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("deliverInfoPro")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("deliverInfoCity")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("deliverInfoDistrict")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("appliPhoneNumber")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("invoiceTitle")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("itemKindFlag")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("2"),
                ParamGeneratorUtil.schemeParam("licenseflag")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("1"),
                ParamGeneratorUtil.schemeParam("certificatedate")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("monopolyname")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("weiFaName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("isRenewal")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("isRenewal", ""),
                ParamGeneratorUtil.schemeParam("interimNo")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("beforeProposalNo")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("taxPayerIdentType")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("carKindCI")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("bjfuel_type")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("certificate_type")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("certificate_no")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("certificate_date")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("carIdentifyAddressSX")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("carNameSX")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("carKindSX")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("ccaId")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("")
        );
    }


    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(realResponse));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            String interimNo = JacksonUtil.getStringNodeByKey(jsonNode, "interimNo");
            logger.info("interimNo获取成功,{}", interimNo);
            context.put("interimNo", interimNo);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("interimNo获取失败,响应为:\n{}", realResponse);
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

