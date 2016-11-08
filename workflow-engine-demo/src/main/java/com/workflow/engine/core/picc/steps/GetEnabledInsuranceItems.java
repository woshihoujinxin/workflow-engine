package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.CollectionUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.getBirthdayFromIdentity;
import static com.workflow.engine.core.common.utils.BusinessUtil.getGenderFromIdentity;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;
import static com.workflow.engine.core.common.utils.JacksonUtil.getJsonNode;
import static com.workflow.engine.core.common.utils.JacksonUtil.getListNodeByKey;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;
import static com.workflow.engine.core.picc.config.Constants._KIND_CODE_MAPPINGS;
import static com.workflow.engine.core.picc.config.Constants._NECESSARY_INFO_MAPPINGS;
import static com.workflow.engine.core.picc.util.PiccBizUtil.handleResponse;
import static com.workflow.engine.core.picc.util.PiccBizUtil.sendPostRequest;

/**
 * 获取可选套餐
 * Created by houjinxin on 16/3/9.
 */
public class GetEnabledInsuranceItems implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(GetEnabledInsuranceItems.class);

    private static final String _API_CALCULATE_FEE = "http://www.epicc.com.cn/wap/carProposal/calculateFee/fee";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map headers = PiccBizUtil.getHeaders();
        headers.put("Referer", "http://www.epicc.com.cn/wap/carProposal/car/calculateFee");
        Map<String, String> requestParams = getRequestParams(context);
//        String response = executePostMethod(_API_CALCULATE_FEE, requestParams, headers);
        Map<String, Object> responseMap = handleResponse(sendPostRequest(requestParams, _API_CALCULATE_FEE));
        String response = (String) responseMap.get("response");
        if ((boolean) responseMap.get("isJson")) {
            JsonNode jsonNode = getJsonNode(JsonHelper.convert(response));
            String resultCode = getStringNodeByKey(jsonNode, "resultCode");
            if ("1".equals(resultCode)) {
                getEnabledInsuranceItems(jsonNode, context);
                getDisabledInsuranceItems(context);
                getNecessaryAmountFormInsuranceItems(context);
                return successfulStepState(this);
            } else {
                logger.info("获取可选套餐失败,响应为: \n{}", response);
                return failureStepState(this);
            }
        } else {
            if ("2".equals(response)) {
                logger.info("链接平台超时,请重试");
                return failureStepState(this, MsgEnum.TimeOut);
            } else {
                logger.info("未知原因出错,联系客服人工报价!,响应为:\n{}", response);
                return failureStepState(this, MsgEnum.Other);
            }
        }

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
            for (String[] kindCodeMapping : _NECESSARY_INFO_MAPPINGS) {
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
     * //TODO: 收集amountList为空的险种,做特殊处理 将这些险种在请求中的参数置为空
     *
     * @param jsonNode 响应JSON
     */
    public void getEnabledInsuranceItems(JsonNode jsonNode, Map<String, Object> context) throws IOException {
        List<Map<String, String>> items = getListNodeByKey(jsonNode, "items");
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
        for (String[] kindCodeMapping : _KIND_CODE_MAPPINGS) {
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

    public Map<String, String> getRequestParams(Map<String, Object> context) throws ParseException {
        String sessionId = (String) context.get("sessionId");
        String licenseNo = (String) context.get("licenseNo");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        String enrollDate = (String) context.get("enrollDate");
        String engineNo = (String) context.get("engineNo");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        String insuredName = (String) context.get("carOwner");
        String identifyNumber = (String) context.get("identity");
        String identifyType = "01";
        String birthday = getBirthdayFromIdentity(identifyNumber, "yyyy/MM/dd");
        String sex = getGenderFromIdentity(identifyNumber, "1", "2");
        String guohuselect = "0"; //是否是过户车,默认不是
        String isRenewal = (String) context.get("isRenewal"); // 0 转保, 1 续保, 2 新车未上牌
        String transferDate = context.get("transferdate") == null ? DateUtils.format(new Date(), "yyyy/MM/dd") : (String) context.get("transferdate");
        String seatFlag = context.get("seatFlag") == null ? "" : (String) context.get("seatFlag");

        String seatCount;
        String appliEmail;
        String appliMobile;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        if ((boolean) context.get("reuseCarData")) {
            Map<String, String> appliInfo = (Map<String, String>) vehicleInfo.get("appliInfo");
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            seatCount = appliCarInfo.get("seat");
            appliEmail = appliInfo.get("appliEmail");
            appliMobile = appliInfo.get("appliMobile");
        } else {
            appliEmail = "woshihoujinxin@163.com";
            appliMobile = "18911568334";
            seatCount = (String) vehicleInfo.get("seat");
        }

        Map<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("areaCodeLast", proSelected);  //上年投保省
        requestParams.put("cityCodeLast", citySelected); //上年投保市
        requestParams.put("mobile", appliMobile);
        requestParams.put("email", appliEmail);
        requestParams.put("identifytype", identifyType);
        requestParams.put("identifynumber", identifyNumber);
        requestParams.put("birthday", birthday);
        requestParams.put("sex", sex);
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected); //
        requestParams.put("startdate", startDate); // 当天的下一天
        requestParams.put("starthour", "0"); //
        requestParams.put("enddate", endDate); //
        requestParams.put("endhour", "24"); //
        requestParams.put("isRenewal", isRenewal); // 默认是0 2为新车未上牌
        requestParams.put("licenseno", licenseNo); //
        requestParams.put("nonlocalflag", "0"); // 是否为外地车 0 不是
        requestParams.put("licenseflag", "1"); //
        requestParams.put("engineno", engineNo); //
        requestParams.put("vinno", vinNo); //
        requestParams.put("frameno", frameNo); //
        requestParams.put("newcarflag", "0"); //默认值
        requestParams.put("isOutRenewal", "0"); // 如果 isRenewal为0 那么 为0
        requestParams.put("lastHas050200", "0"); // 如果 isRenewal为0 那么 为0
        requestParams.put("lastHas050210", "0"); // 如果 isRenewal为0 那么 为0
        requestParams.put("lastHas050500", "0"); // 如果 isRenewal为0 那么 为0
        requestParams.put("enrolldate", enrollDate); //
        requestParams.put("transfervehicleflag", "0"); //
        requestParams.put("insuredname", insuredName); //
        requestParams.put("fullAmountName", ""); //
        requestParams.put("seatCount", seatCount); //
        requestParams.put("transferdate", transferDate); //
        requestParams.put("sessionId", sessionId);
        requestParams.put("seatflag", seatFlag); //
        requestParams.put("guohuselect", guohuselect); //
        requestParams.put("runAreaCodeName", ""); //
        requestParams.put("assignDriver", "2"); //是否指定驾驶员  1指定 2不指定
        requestParams.put("haveLoan", "2"); //是否贷款车 1是  2 不是
        requestParams.put("LoanName", ""); //
        requestParams.put("weiFaName", ""); //
        requestParams.put("carDrivers", ""); //
        requestParams.put("ccaFlag", ""); //
        requestParams.put("ccaID", ""); //
        requestParams.put("ccaEntryId", ""); //
        requestParams.put("travelMilesvalue", ""); //
        return requestParams;
    }

}

