package com.workflow.engine.core.pingan.steps.n;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.CollectionUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.pingan.config.Constants;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executeGetMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;

/**
 * 判断车辆价格是否含税
 * Created by houjinxin on 16/3/9.
 */
public class CheckVehiclePriceHasTax extends PinganAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(CheckVehiclePriceHasTax.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/autox/do/api/to-query-info?";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("department.cityCode")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityAreaCode", ""),
                ParamGeneratorUtil.schemeParam("vehicle.licenseNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("licenseNo", ""),
                ParamGeneratorUtil.schemeParam("partner.mediaSources")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("sc03-direct-mpingan"),
                ParamGeneratorUtil.schemeParam("partner.partnerName")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("chexian-mobile"),
                ParamGeneratorUtil.schemeParam("flowId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("flowId", ""),
                ParamGeneratorUtil.schemeParam("__xrc")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("xrc", ""),
                ParamGeneratorUtil.schemeParam("renewal.idNo")
                        .valueSourceExpression("$val(context, isRenewal)")
                        .chooseStrategyIfTrue(StrategyType.METHOD)
                        .configStrategyNeedParamsIfTrue("$getLastSixNumberFromIdentity(identity, \"\", \"\")")
                        .chooseStrategyIfFalse(StrategyType.NULL)
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
            checkHasTax(context, jsonNode);
            boolean isRenewal = (boolean) context.get("isRenewal");
            if (isRenewal) { //续保用户车型信息从响应数据中获取
                getVehicleInfo(context, jsonNode);
                return BusinessUtil.successfulStepState(this, "续保");
            } else {
                return BusinessUtil.successfulStepState(this, "转保");
            }
        } else if (resultCode != null && resultCode.equals(Constants._RESULT_CODE_C0006)) {
            logger.info("续保用户信息出错(身份证后六位)，响应为:{}", JacksonUtil.getStringNodeByKey(jsonNode, "resultMessage"));
            return BusinessUtil.failureStepState(this, MsgEnum.WrongIdentity);
        } else {
            logger.info("判断车辆价格是否含税失败，响应为:\n{}", realResponse);
            return BusinessUtil.failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    /**
     * 检查车辆是否含税
     *
     * @param context
     * @param jsonNode
     * @throws IOException
     */
    private void checkHasTax(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        Map<String, String> switches = JacksonUtil.getMapNodeByKey(jsonNode, "switches");
        String isUseTaxPrice = switches.get("isUseTaxPrice");
        boolean hasTax;
        if (isUseTaxPrice != null) {
            hasTax = Boolean.parseBoolean(isUseTaxPrice);
        } else {
            hasTax = false;
        }
        logger.info("该车价格{}", hasTax ? "含税" : "不含税");
        context.put("hasTax", hasTax);
    }

    /**
     * 续保车辆获取车型信息,主要内容如下:
     * 初登日期(vehicle.registerDate)
     * 车型号(vehicle.model)
     * 品牌型号(vehicle.modelName)
     * 配置型号(vehicle.vehicleId) 车型的唯一标识(例:402880882158e5fc0121768e6d8b0d6c)
     * 车架号(vehicle.frameNo) 含"*"
     * 发动机号(vehicle.engineNo) 含"*"
     *
     * @param context
     * @param jsonNode
     * @throws IOException
     */
    private void getVehicleInfo(Map<String, Object> context, JsonNode jsonNode) throws IOException {
        List<Map<String, String>> vehicleInfoList = JacksonUtil.getListNodeByKey(jsonNode, "vehicle");
        Map<String, String> vehicleInfoMap = CollectionUtil.collect(vehicleInfoList, "name", "value");
        context.put("vehicleInfo", vehicleInfoMap);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

