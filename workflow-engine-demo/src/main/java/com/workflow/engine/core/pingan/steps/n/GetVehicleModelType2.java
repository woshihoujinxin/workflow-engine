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
 * Created by houjinxin on 16/4/28.
 */
public class GetVehicleModelType2 extends AbstractGetVehicleModel {

    private static final Logger logger = LoggerFactory.getLogger(GetVehicleModelType2.class);

    @Override
    protected String getRequestUrl() {
        return "http://u.pingan.com/rsupport/vehicle/model-brand?";
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
                        .configStrategyNeedParams("$getYearAndMonthFromDate(enrollDate, \"-\")"),
                ParamGeneratorUtil.schemeParam("brand_name")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("brand_name", "")
//                schemeParam("family_name")
//                        .chooseStrategy(StrategyType.FIXED)
//                        .configStrategyNeedParams(""),
//                schemeParam("engine_desc")
//                        .chooseStrategy(StrategyType.FIXED)
//                        .configStrategyNeedParams(""),
//                schemeParam("gearbox_name")
//                        .chooseStrategy(StrategyType.FIXED)
//                        .configStrategyNeedParams(""),
//                schemeParam("vehicle_fgw_code")
//                        .chooseStrategy(StrategyType.FIXED)
//                        .configStrategyNeedParams("")
        );
    }

    @Override
    protected StepState vehiclesNotFound() {
        logger.info("未能查到车型, 可修改查询条件重新查找车型");
        return failureStepState(this, MsgEnum.QueryCar);
    }

    @Override
    protected StepState vehiclesFound() {
        return successfulStepState(this);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
