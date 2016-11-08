package com.workflow.engine.core.pingan.steps;


import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.CollectionUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.pingan.utils.PinganBizUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executeGetMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;
import static com.workflow.engine.core.pingan.config.Constants._NECESSARY_INFO_MAPPINGS;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C0000;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C2003;
import static com.workflow.engine.core.pingan.config.Constants._RESULT_CODE_C3009;

/**
 * 提交车辆信息,获取可选套餐列表
 * Created by houjinxin on 16/3/9.
 */
public class SaveQuoteInfo implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(SaveQuoteInfo.class);

    //获得车辆报价基础信息
    private static final String _API_SAVE_QUOTE_INFO = "http://u.pingan.com/autox/do/api/save-quote-info?";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = (boolean) context.get("isRenewal")
                ? getRenewalRequestParams(context) : getNotRenewalRequestParams(context);
        String response = BusinessUtil.executeGetMethod(_API_SAVE_QUOTE_INFO, requestParams, PinganBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(response);
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if (resultCode != null && resultCode.equals(_RESULT_CODE_C0000)) {
            Map<String, String> circResult = JacksonUtil.getMapNodeByKey(jsonNode, "circResult");
            if(_RESULT_CODE_C3009.equals(circResult.get("resultCode"))) { //过户车
                context.put("transferYesOrNo", true);
                logger.info("该车是过户车,需要将请求中的bizInfo.specialCarFlag和bizInfo.specialCarDate的值修改,并重发请求");
                return BusinessUtil.successfulStepState(this, "过户车");
            }
            //用户出险记录
            getCircResult(context, jsonNode);
            //获取可选的险种信息
            getEnabledInsuranceItems(context, jsonNode);
            //获取inputAmount
            getNecessaryAmountFormInsuranceItems(context, jsonNode);
            //交强险起保日期
            getForceBeginDate(context, jsonNode);
            //商业险起保日期
            getBizBeginDate(context, jsonNode);
            return BusinessUtil.successfulStepState(this);
        } else if (resultCode != null && resultCode.equals(_RESULT_CODE_C2003)) {
            logger.info("提交的车型信息可能有误,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this, MsgEnum.QueryCar);
        } else {
            logger.info("获取该车可选套餐失败，响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    /**
     * 获取出险记录
     * @param context
     * @param jsonNode
     * @throws IOException
     */
    private void getCircResult(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        context.put("circResult", JacksonUtil.getMapNodeByKey(jsonNode, "circResult"));
    }

    /**
     * 获取可选的险种信息
     */
    private void getEnabledInsuranceItems(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        //商业险相关信息
        List<Map<String, String>> bizInfoList = CollectionUtil.collect(JacksonUtil.getListNodeByKey(jsonNode, "bizConfig"), new String[]{"name", "text", "option"}, new CollectionUtil.DataFilter() {
            @Override
            public boolean doFilter(Object... objs) {
                Object value = objs[1];
                if (value == null) { //不关心具体类型值要排除null值, 这里的objs[1]可能为String 可能为List
                    return true;
                } else if (value instanceof String) {
                    return value.equals("bizConfig.pkgName");
                } else {
                    return false;
                }
            }
        });
        logger.info("获取可选套餐成功:\n{}", bizInfoList);
        context.put("enabledInsuranceList", bizInfoList);
//        Map textToParams = collect(bizInfoList, "text", "name");
//        logger.info("套餐文本描述与字段的对应关系为:\n{}", textToParams);
    }

    /**
     * 从响应中获取inputAmount的值,这个值一般情况代表了车损,盗抢,自燃的保额,但是平安M站是用1和0表示
     *
     * @param context
     */
    private void getNecessaryAmountFormInsuranceItems(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        List<Map<String, String>> children = JacksonUtil.getListNodeByKey(jsonNode, "children");
        String bizConfigInputAmount = "";
        for (Map<String, String> map : children) {
            if (map.get("name") != null && map.get("name").equals("bizConfig.inputAmount")) {
                bizConfigInputAmount = map.get("value");
            }
        }
        context.put("inputAmount", bizConfigInputAmount);
        logger.info("成功获取inputAmount,值为{}", bizConfigInputAmount);
        for (String[] necessaryMapping : _NECESSARY_INFO_MAPPINGS) {
            context.put(necessaryMapping[2], bizConfigInputAmount);
            logger.info("成功获取必要信息,{}值为{}", necessaryMapping[1], bizConfigInputAmount);
        }
    }

    /**
     * 获取交强险起保日期
     *
     * @param context
     * @param jsonNode
     * @throws IOException
     */
    private void getForceBeginDate(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        //交强险相关信息
        List<Map<String, String>> forceInfoList = CollectionUtil.collect(JacksonUtil.getListNodeByKey(jsonNode, "forceConfig"), new String[]{"name", "value", "text"});
        Map forceInfo = CollectionUtil.collect(forceInfoList, "name", "value");
        String forceBeginDate = (String) forceInfo.get("forceInfo.beginDate");
        context.put("forceBeginDate", forceBeginDate);
        logger.info("交强险起保日期为:{}", forceBeginDate);
    }

    /**
     * 获取商业险起保日期
     *
     * @param context
     * @param jsonNode
     * @throws IOException
     */
    private void getBizBeginDate(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        List<Map<String, String>> bizInfoList = CollectionUtil.collect(JacksonUtil.getListNodeByKey(jsonNode, "bizConfig"), new String[]{"name", "value"});
        String bizBeginDate = "";
        for (Map<String, String> map : bizInfoList) {
            if (map.get("name") != null && map.get("name").equals("bizInfo.beginDate")) {
                bizBeginDate = map.get("value");
            }
        }
        context.put("bizBeginDate", bizBeginDate);
        logger.info("商业险起保日期为:{}", bizBeginDate);
    }


    private Map<String, String> getNotRenewalRequestParams(Map<String, Object> context) throws Exception {
        String flowId = (String) context.get("flowId");
        String xrc = (String) context.get("xrc");
        String frameNo = (String) context.get("frameNo");
        String engineNo = (String) context.get("engineNo");
        String engineeNo = engineNo.substring(engineNo.length() - 4, engineNo.length()); //发动机号后四位 应该是可选的
        String carOwner = (String) context.get("carOwner");
        String enrollDate = (String) context.get("enrollDate");
        String identity = (String) context.get("identity");
        String gender = BusinessUtil.getGenderFromIdentity(identity, "M", "F");

        Map<String, String> vehicleInfo = (Map<String, String>) context.get("vehicleInfo");
        String vehicleId = vehicleInfo.get("vehicle_id");
        String modelName = vehicleInfo.get("standard_name");
        String seatCount = "0".equals(vehicleInfo.get("seat")) ? "5" : vehicleInfo.get("seat");
        boolean transferYesOrNo = (boolean)context.put("transferYesOrNo", true);
        String specialCarDate = DateUtils.format(new Date(), "yyyy-MM-dd");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("flowId", flowId);
        requestParams.put("bizConfig.pkgName", "optional");
        requestParams.put("vehicle.engineeNo", engineeNo); //发动机号后四位 应该是可选的
        requestParams.put("vehicle.frameNo", frameNo);
        requestParams.put("vehicle.engineNo", engineNo);
        requestParams.put("vehicle.vehicleId", vehicleId);
        requestParams.put("vehicle.modelName", modelName);
        requestParams.put("vehicle.registerDate", enrollDate);
        requestParams.put("vehicle.inputSeatFlag", "0");
        requestParams.put("register.name", carOwner);
        requestParams.put("register.gender", gender);
        requestParams.put("bizInfo.specialCarFlag", transferYesOrNo ? "1" : "0");
        requestParams.put("bizInfo.specialCarDate", transferYesOrNo ? specialCarDate : "");
        requestParams.put("__xrc", xrc);
        requestParams.put("vehicle.seat", seatCount);

        //待定参数
//        requestParams.put("register.mobile"       ,"18911568334");
//        requestParams.put("applicant.mobile"      ,"18911568334");
//        requestParams.put("bizInfo.kilometrePerYear"      ,"20000");
//        requestParams.put("forceInfo.fuelType"      ,"A");
//        requestParams.put("address.confirmSendFlag"      ,"1");
        return requestParams;
    }

    private Map<String, String> getRenewalRequestParams(Map<String, Object> context) throws Exception {
        String flowId = (String) context.get("flowId");
        String xrc = (String) context.get("xrc");
        String carOwner = (String) context.get("carOwner");
        String enrollDate = (String) context.get("enrollDate");
        String identity = (String) context.get("identity");
        String gender = BusinessUtil.getGenderFromIdentity(identity, "M", "F");
        String birthday = BusinessUtil.getBirthdayFromIdentity(identity, "yyyy-MM-dd");

        Map<String, String> vehicleInfo = (Map<String, String>) context.get("vehicleInfo");
        String vehicleId = vehicleInfo.get("vehicle.vehicleId");
        String modelName = vehicleInfo.get("vehicle.modelName");
        String model = vehicleInfo.get("vehicle.model");

        boolean transferYesOrNo = (boolean)context.put("transferYesOrNo", true);
        String specialCarDate = DateUtils.format(new Date(), "yyyy-MM-dd");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("flowId", flowId);
        requestParams.put("bizConfig.pkgName", "optional");
        requestParams.put("vehicle.vehicleId", vehicleId);
        requestParams.put("vehicle.model", model);
        requestParams.put("vehicle.modelName", modelName);
        requestParams.put("vehicle.registerDate", enrollDate);
        requestParams.put("register.name", carOwner);
        requestParams.put("register.gender", gender);
        requestParams.put("register.idType", "01");
        requestParams.put("register.birthday", birthday);
        requestParams.put("bizInfo.specialCarFlag", transferYesOrNo ? "1" : "0");
        requestParams.put("bizInfo.specialCarDate", transferYesOrNo ? specialCarDate : "");
        requestParams.put("__xrc", xrc);
        return requestParams;
    }

}

