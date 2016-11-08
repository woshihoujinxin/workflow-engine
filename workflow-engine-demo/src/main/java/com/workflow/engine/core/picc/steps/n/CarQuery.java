package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 查询车辆信息
 * Created by houjinxin on 16/3/9.
 */
public class CarQuery extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(CarQuery.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/carSelect/carQuery";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("proSelected")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.areaCode", "11000000"),
                ParamGeneratorUtil.schemeParam("citySelected")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("cityCodeMapping.{cityAreaCode}.cityCode", "11000000"),
                ParamGeneratorUtil.schemeParam("carOwner")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("carOwner", ""),
                ParamGeneratorUtil.schemeParam("licenseNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("licenseNo", ""),
                ParamGeneratorUtil.schemeParam("queryCode")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("queryCode", ""),
                ParamGeneratorUtil.schemeParam("engineNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("engineNo", ""),
                ParamGeneratorUtil.schemeParam("enrollDate")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("enrollDate", ""),
                ParamGeneratorUtil.schemeParam("frameNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("frameNo", ""),
                ParamGeneratorUtil.schemeParam("licenseFlag")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("1"),
                ParamGeneratorUtil.schemeParam("vinNo")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("frameNo", ""),
                ParamGeneratorUtil.schemeParam("isRenewal")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("isRenewal", ""),
                ParamGeneratorUtil.schemeParam("sessionId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("sessionId", "")
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
            List<Map<String, String>> vehicleInfoList = JacksonUtil.getListNodeByKey(jsonNode, "queryVehicle");
            logger.info("车型查询成功,结果为:\n{}", vehicleInfoList);
            Map<String, String> vehicleInfo = vehicleInfoList.get(0); //获取第一辆车
            context.put("vehicleInfo", vehicleInfo);
            return BusinessUtil.successfulStepState(this);
        } else if ("3".equals(resultCode)) {
            logger.info("没有对应车辆信息,请求确认输入信息是否正确");
            return BusinessUtil.failureStepState(this, MsgEnum.QueryCar);
        } else {
            logger.info("车型查询失败,结果为:\n{}", realResponse);
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

