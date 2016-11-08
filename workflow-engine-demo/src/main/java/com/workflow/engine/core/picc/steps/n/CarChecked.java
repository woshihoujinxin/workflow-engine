package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;

/**
 * 校验车型
 * Created by houjinxin on 16/3/9.
 */
public class CarChecked extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(CarChecked.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/carSelect/carChecked";
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
                ParamGeneratorUtil.schemeParam("parentId")
                        .valueSourceExpression("$val(context, \"reuseCarData\") && $val(context, \"cityIsBeijing\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.parentId", "0")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.parentId", ""),
                ParamGeneratorUtil.schemeParam("sessionId")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("sessionId", ""),
                ParamGeneratorUtil.schemeParam("modelCode")
                        .valueSourceExpression("$val(context, \"reuseCarData\") && $val(context, \"cityIsBeijing\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.brandName", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.modelCode", ""),
                ParamGeneratorUtil.schemeParam("countryNature")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams(""),
                ParamGeneratorUtil.schemeParam("seatCount")
                        .valueSourceExpression("$val(context, \"reuseCarData\") && $val(context, \"cityIsBeijing\")")
                        .chooseStrategyIfTrue(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfTrue("vehicleInfo.appliCarInfo.seat", "")
                        .chooseStrategyIfFalse(StrategyType.CONTEXT)
                        .configStrategyNeedParamsIfFalse("vehicleInfo.seatCount", "")
        );
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(realResponse));
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            logger.info("校验车辆成功,结果为:\n{}", realResponse);
            return successfulStepState(this);
        } else {
            logger.info("校验车辆失败,结果为:\n{}", realResponse);
            return failureStepState(this);
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

