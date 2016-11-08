package com.workflow.engine.core.pingan.steps;

import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 第一个接口能返回完整车型,则不需要第二个接口
 * Created by houjinxin on 16/4/28.
 */
public class GetVehicleModelType1 extends AbstractGetVehicleModel {

    private static final Logger logger = LoggerFactory.getLogger(GetVehicleModelType1.class);

    private static final String _API_BRAND = "http://u.pingan.com/rsupport/vehicle/brand?";

    @Override
    protected String getApiPath() {
        return _API_BRAND;
    }

    @Override
    protected Map<String, String> getRequestParams(Map<String, Object> context) {
        String k = (String) context.get("brandCode");
        //TODO:目前只能查询第一页,实际上应当查询全部,以供用户去选择
        String page = "1";
        String market_date = ""; //允许为空

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("k", k);
        requestParams.put("page", page);
        requestParams.put("market_date", market_date);
        return requestParams;
    }

    @Override
    protected StepState vehiclesNotFound() {
        logger.info("未能查到车型, 可修改查询条件重新查找车型");
        return BusinessUtil.failureStepState(this, MsgEnum.QueryCar);
    }

    /**
     * 接口一返回成功,一般需要继续访问第二个接口,在这个方法里可以加入一些控制逻辑,判断段是否要走第二个接口,
     * 此处实现为一点会走第二个接口
     * @return
     */
    @Override
    protected StepState vehiclesFound() {
        return BusinessUtil.successfulStepState(this, "需要接口二");
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
