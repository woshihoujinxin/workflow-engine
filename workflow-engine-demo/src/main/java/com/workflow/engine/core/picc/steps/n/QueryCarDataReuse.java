package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 查询车辆数据可复用
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "beforeProposalNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"identity", ""}),
        @PGScheme(requestParamName = "licenseNo", strategy = StrategyType.CONTEXT, strategyNeedParams = {"licenseNo", ""}),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""})
})
public class QueryCarDataReuse extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(QueryCarDataReuse.class);

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/car/query_carDataReuse";
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return BusinessUtil.executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(realResponse);
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode) || "5".equals(resultCode)) {
            Map<String, String> vehicleInfo = JacksonUtil.getMapNodeByKey(jsonNode, "message");
            logger.info("车型信息:{}", vehicleInfo);
            context.put("vehicleInfo", vehicleInfo);
            //TODO:加入判断是否是新车的逻辑,现在默认都是新车
            if ((boolean) context.get("cityIsBeijing")) {
                logger.info("当前城市为北京, 北京的新车需要单独查询一次车型");
                context.put("reuseCarData", false);
                return BusinessUtil.successfulStepState(this, "北京市新车单独查询车型");
            }
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("车型信息查询失败,原因为证件号码不匹配,响应为:{}", realResponse);
            return BusinessUtil.failureStepState(this, MsgEnum.WrongIdentity);
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

