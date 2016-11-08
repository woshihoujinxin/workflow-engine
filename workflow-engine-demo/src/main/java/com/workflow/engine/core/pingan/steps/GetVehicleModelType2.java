package com.workflow.engine.core.pingan.steps;

import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * Created by houjinxin on 16/4/28.
 */
public class GetVehicleModelType2 extends AbstractGetVehicleModel {

    private static final Logger logger = LoggerFactory.getLogger(GetVehicleModelType2.class);

    private static final String _API_MODEL_BRAND = "http://u.pingan.com/rsupport/vehicle/model-brand?";

    @Override
    protected String getApiPath() {
        return _API_MODEL_BRAND;
    }

    @Override
    protected Map<String, String> getRequestParams(Map<String, Object> context) {
        String k = (String) context.get("brandCode");
        //TODO:目前只能查询第一页,实际上应当查询全部,以供用户去选择
        String page = "1";
        String market_date = ""; //允许为空

        String brand_name = (String) context.get("brand_name");
        String family_name = "";
        String engine_desc = "";
        String gearbox_name = "";
        String vehicle_fgw_code = "";

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("k", k);
        requestParams.put("page", page);
        requestParams.put("market_date", market_date);
        requestParams.put("brand_name", brand_name);
        requestParams.put("family_name", family_name);
        requestParams.put("engine_desc", engine_desc);
        requestParams.put("gearbox_name", gearbox_name);
        requestParams.put("vehicle_fgw_code", vehicle_fgw_code);
        return requestParams;
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
