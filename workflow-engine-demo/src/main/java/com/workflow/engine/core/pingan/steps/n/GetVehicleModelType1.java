package com.workflow.engine.core.pingan.steps.n;

import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 第一个接口能返回完整车型,则不需要第二个接口
 * Created by houjinxin on 16/4/28.
 */
public class GetVehicleModelType1 extends AbstractGetVehicleModel {

    private static final Logger logger = LoggerFactory.getLogger(GetVehicleModelType1.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/rsupport/vehicle/brand?";
    }

    @Override
    protected ParamGeneratorConfig stepNeededParamsConfig() {
        return ParamGeneratorUtil.configParamScheme(
                ParamGeneratorUtil.schemeParam("k")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("brandCode", ""),
                ParamGeneratorUtil.schemeParam("page")
                        .chooseStrategy(StrategyType.FIXED)
                        .configStrategyNeedParams("1"),
                ParamGeneratorUtil.schemeParam("market_date")
                        .chooseStrategy(StrategyType.METHOD)
                        .configStrategyNeedParams("$getYearAndMonthFromDate(enrollDate, \"-\")")
        );
    }

    @Override
    protected StepState vehiclesNotFound() {
        logger.info("未能查到车型, 可修改查询条件重新查找车型");
        return failureStepState(this, MsgEnum.QueryCar);
    }

    /**
     * 接口一返回成功,一般需要继续访问第二个接口,在这个方法里可以加入一些控制逻辑,判断段是否要走第二个接口,
     * 此处实现为一点会走第二个接口
     *
     * @return
     */
    @Override
    protected StepState vehiclesFound() {
        return successfulStepState(this, "需要接口二");
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
