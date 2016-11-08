package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.CollectionUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.config.Constants;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;

/**
 * 获取可选套餐
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "areaCodeLast", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "cityCodeLast", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "mobile", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.appliEmail", "18911111111"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"", "18911111111"}),
        @PGScheme(requestParamName = "email", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.appliMobile", "121@qq.com"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"", "121@qq.com"}),
        @PGScheme(requestParamName = "identifytype", strategy = StrategyType.FIXED, strategyNeedParams = "01"),
        @PGScheme(requestParamName = "identifynumber", strategy = StrategyType.CONTEXT, strategyNeedParams = {"identity", ""}),
        @PGScheme(requestParamName = "birthday", strategy = StrategyType.METHOD, strategyNeedParams = "$getBirthdayFromIdentity(identity, \"yyyy/MM/dd\")"),
        @PGScheme(requestParamName = "sex", strategy = StrategyType.METHOD, strategyNeedParams = "$getGenderFromIdentity(identity, \"1\", \"2\")"),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
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
        @PGScheme(requestParamName = "seatcount", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.seat", "5"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"vehicleInfo.seat", "5"}),
        @PGScheme(requestParamName = "transferdate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"transferDate", "$formatDate($SYSDATE(),\"yyyy-MM-dd\")"}),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""}),
        @PGScheme(requestParamName = "seatflag", strategy = StrategyType.CONTEXT, strategyNeedParams = {"seatFlag", ""}),
        @PGScheme(requestParamName = "guohuselect", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "runAreaCodeName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "assignDriver", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "haveLoan", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "LoanName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "weiFaName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "carDrivers", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaFlag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaID", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaEntryId", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "travelMilesvalue", strategy = StrategyType.FIXED, strategyNeedParams = "")
})
public class GetEnabledInsuranceItems extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(GetEnabledInsuranceItems.class);

    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = super.getHeaders();
        headers.put("Referer", "http://www.epicc.com.cn/wap/carProposal/car/calculateFee");
        return headers;
    }

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/calculateFee/fee";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.sendPostRequest(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(realResponse));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            getEnabledInsuranceItems(jsonNode, context);
            getDisabledInsuranceItems(context);
            getNecessaryAmountFormInsuranceItems(context);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("获取可选套餐失败,响应为: \n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        if ("2".equals(realResponse)) {
            logger.info("链接平台超时,请重试");
            return BusinessUtil.failureStepState(this, MsgEnum.TimeOut);
        } else {
            logger.info("未知原因出错,联系客服人工报价!,响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this, MsgEnum.Other);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * 从可选险种列表中获取车损,盗抢,自燃三个险种的保额,并放入上下文,若有险种不可选,那么不会放入上下文,
     * 故在使用这几个保额是需要判断是否为空
     *
     * @param context
     */
    public void getNecessaryAmountFormInsuranceItems(Map<String, Object> context) {
        List<Map<String, String>> enabledInsuranceList = (List<Map<String, String>>) context.get("enabledInsuranceList");
        for (Map<String, String> insurance : enabledInsuranceList) {
            for (String[] kindCodeMapping : Constants._NECESSARY_INFO_MAPPINGS) {
                if (insurance.get("kindCode").equals(kindCodeMapping[1])) {
                    String amountList = insurance.get("amountList");
                    String amount = amountList.substring(amountList.indexOf("|") + 1, amountList.length() - 1);
                    logger.info("{}的保额是{}", kindCodeMapping[0], amount);
                    context.put(kindCodeMapping[2], amount);
                }
            }

        }
    }

    /**
     * 获取可选的险种信息
     * //TODO: 收集amountList为空的险种,做特殊处理 -  将这些险种在请求中的参数置为空
     *
     * @param jsonNode 响应JSON
     */
    public void getEnabledInsuranceItems(JsonNode jsonNode, Map<String, Object> context) throws IOException {
        List<Map<String, String>> items = JacksonUtil.getListNodeByKey(jsonNode, "items");
        List<Map<String, String>> enabledInsuranceList = CollectionUtil.collect(items, new String[]{"amountList", "kindCode"}, new CollectionUtil.DataFilter() {
            @Override
            public boolean doFilter(Object... objs) {
                String key = (String) objs[0];
                String value = (String) objs[1];
                //排除key为amountList但是value为空的Entry
                return "amountList".equals(key) && value.equals("");
            }
        });
        logger.info("获取可选险种成功:\n{}", enabledInsuranceList);
        context.put("enabledInsuranceList", enabledInsuranceList);
    }

    /**
     * 获取不可选的险种信息, 用所有险种作为总集合, 可选险种作为一个子集, 求得另一个子集,即不可选险种集.
     *
     * @param context
     * @throws IOException
     */
    public void getDisabledInsuranceItems(Map<String, Object> context) throws IOException {
        List<Map<String, String>> enabledInsuranceList = (List<Map<String, String>>) context.get("enabledInsuranceList");
        Map<String, String> kindCode2AmountListMap = CollectionUtil.collect(enabledInsuranceList, "kindCode", "amountList");

        Set<String> allKindCodeSet = new LinkedHashSet<String>();
        for (String[] kindCodeMapping : Constants._KIND_CODE_MAPPINGS) {
            allKindCodeSet.add(kindCodeMapping[1]);
        }
        Set enabledKindCodeSet = kindCode2AmountListMap.keySet();
        allKindCodeSet.removeAll(enabledKindCodeSet);
        List<Map<String, String>> disabledInsuranceList = new ArrayList<Map<String, String>>();
        for (String kindCode : allKindCodeSet) {
            Map<String, String> kindCodeMap = new LinkedHashMap<>();
            kindCodeMap.put("kindCode", kindCode);
            disabledInsuranceList.add(kindCodeMap);
        }
        logger.info("获取不可选险种成功:\n{}", disabledInsuranceList);
        context.put("disabledInsuranceList", disabledInsuranceList);
    }
}

