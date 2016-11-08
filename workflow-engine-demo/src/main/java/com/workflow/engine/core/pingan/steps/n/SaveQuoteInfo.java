package com.workflow.engine.core.pingan.steps.n;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.CollectionUtil;
import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.pingan.config.Constants;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executeGetMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;

/**
 * 提交车辆信息,获取可选套餐列表
 * Created by houjinxin on 16/3/9.
 */
public class SaveQuoteInfo extends PinganAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(SaveQuoteInfo.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/autox/do/api/save-quote-info?";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("flowId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("flowId", ""),
                ParamGeneratorUtil.schemeParam("bizConfig.pkgName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("optional"),
                ParamGeneratorUtil.schemeParam("vehicle.engineeNo")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.NULL)
                        .chooseStrategyIfFalse(StrategyType.METHOD)
                        .configStrategyNeedParamsIfFalse("$getEngineNoLastFourNumber(engineNo)"),
                ParamGeneratorUtil.schemeParam("vehicle.frameNo")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.NULL)
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("frameNo", ""),
                ParamGeneratorUtil.schemeParam("vehicle.engineNo")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.NULL)
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("engineNo", ""),
                ParamGeneratorUtil.schemeParam("vehicle.vehicleId")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.vehicle#vehicleId", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.vehicle_id", ""),
                ParamGeneratorUtil.schemeParam("vehicle.model")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.vehicle#mode", "")
                        .chooseStrategyIfFalse(StrategyType.NULL),
                ParamGeneratorUtil.schemeParam("vehicle.modelName")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.vehicle#modelName", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.standard_name", ""),
                ParamGeneratorUtil.schemeParam("vehicle.registerDate")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("enrollDate", ""),
                ParamGeneratorUtil.schemeParam("vehicle.inputSeatFlag")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.NULL)
                        .chooseStrategyIfFalse(StrategyType.FIXED)
                        .configStrategyNeedParamsIfFalse("0"),
                ParamGeneratorUtil.schemeParam("register.idType")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.FIXED)
                        .configStrategyNeedParamsIfTrue("01")
                        .chooseStrategyIfFalse(StrategyType.NULL),
                ParamGeneratorUtil.schemeParam("register.birthday")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.METHOD)
                        .configStrategyNeedParamsIfTrue("$getBirthdayFromIdentity(identity,\"yyyy-MM-dd\")")
                        .chooseStrategyIfFalse(StrategyType.NULL),
                ParamGeneratorUtil.schemeParam("register.name")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("carOwner", ""),
                ParamGeneratorUtil.schemeParam("register.gender")
                        .chooseStrategy(StrategyType.METHOD)
                        .configStrategyNeedParams("$getGenderFromIdentity(identity,\"M\",\"F\")"),
                ParamGeneratorUtil.schemeParam("bizInfo.specialCarFlag")
                        .valueSourceExpression("$val(context, transferYesOrNo)")
                        .chooseStrategyIfTrue(StrategyType.FIXED)
                        .configStrategyNeedParamsIfTrue("1")
                        .chooseStrategyIfFalse(StrategyType.FIXED)
                        .configStrategyNeedParamsIfFalse("0"),
                ParamGeneratorUtil.schemeParam("bizInfo.specialCarDate")
                        .valueSourceExpression("$val(context, transferYesOrNo)")
                        .chooseStrategyIfTrue(StrategyType.FIXED)
                        .configStrategyNeedParamsIfTrue(DateUtils.format(new Date(), "yyyy-MM-dd"))
                        .chooseStrategyIfFalse(StrategyType.FIXED)
                        .configStrategyNeedParamsIfFalse(""),
                ParamGeneratorUtil.schemeParam("__xrc")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("xrc", ""),
                ParamGeneratorUtil.schemeParam("vehicle.seat")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.NULL)
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.seat", "5")
                        .individualValueSourceExpression("seatCountIsZero")
                        .chooseIndividualStrategy(StrategyType.FIXED)
                        .configIndividualStrategyNeedParams("5")
        );
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.executeGetMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(realResponse);
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if (resultCode != null && resultCode.equals(Constants._RESULT_CODE_C0000)) {
            Map<String, String> circResult = JacksonUtil.getMapNodeByKey(jsonNode, "circResult");
            if (Constants._RESULT_CODE_C3009.equals(circResult.get("resultCode"))) { //过户车
                context.put("$val(context, transferYesOrNo)", true);
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
        } else if (resultCode != null && resultCode.equals(Constants._RESULT_CODE_C2003)) {
            logger.info("提交的车型信息可能有误,响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this, MsgEnum.QueryCar);
        } else {
            logger.info("获取该车可选套餐失败，响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    /**
     * 获取出险记录
     *
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
        for (String[] necessaryMapping : Constants._NECESSARY_INFO_MAPPINGS) {
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


    @Override
    public Logger getLogger() {
        return logger;
    }
}

