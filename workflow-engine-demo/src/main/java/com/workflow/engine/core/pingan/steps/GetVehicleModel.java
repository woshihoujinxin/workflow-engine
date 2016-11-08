package com.workflow.engine.core.pingan.steps;


import com.workflow.engine.core.service.entity.vo.VehicleCriteriaVO;
import com.workflow.engine.core.service.entity.vo.VehicleInfoVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.executeGetMethod;

/**
 * TODO: 重构查询车型流程, 两个接口分成两个步骤,第一个查不到车型才使用第二个,车型查询结果放到上下文中,可以将这两个步骤单独抽成流程链
 * 组合使用
 * 车型查询
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class GetVehicleModel implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(GetVehicleModel.class);
    private static final String _FIRST_REQUEST_URL = "http://u.pingan.com/rsupport/vehicle/brand?";
    private static final String _SECOND_REQUEST_URL = "http://u.pingan.com/rsupport/vehicle/model-brand?";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        VehicleCriteriaVO criteriaVO = (VehicleCriteriaVO) context.get("bizObject");
        VehicleInfoVO vehicleInfoVO = getVehicleInfo(criteriaVO);
        logger.info("车型查询结果为:{}\n", vehicleInfoVO);
        context.put("result", vehicleInfoVO);
        return BusinessUtil.successfulStepState(this);
    }

    public static VehicleInfoVO getVehicleInfo(VehicleCriteriaVO criteriaVO) throws Exception {
        VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();
        Map<String, String> conditions = BeanUtils.describe(criteriaVO);
        BeanUtils.populate(vehicleInfoVO, doGetVehicleInfo(conditions));
        return vehicleInfoVO;
    }

    /**
     * 根据用户选择的查询条件,获取车型信息,车型信息包含一下几个方面:
     * 一是车型列表,
     * 二是车型筛选条件
     * 三是分页信息
     *
     * @param conditions 车型的查询条件,下文所示是一个完整的查询条件,其中k,page,market_date是必选项,其他为可选
     *                   Map<String, String> queryString = new HashMap<String, String>(){{
     *                      put("k", "梅赛德斯");
     *                      put("page", "1");
     *                      put("market_date", "");
     *                      put("brand_name", "北京奔驰");
     *                      put("family_name", "E级");
     *                      put("engine_desc", "1.8T");
     *                      put("gearbox_name", "手自一体");
     *                      put("vehicle_fgw_code", "BJ7182VXL");
     *                   }}
     * @return
     * @throws IOException
     */
    private static Map<String, Object> doGetVehicleInfo(Map<String, String> conditions) throws Exception {
        Map<String, Object> vehicleInfo = new HashMap<>();
        //以下三个是必有字段
        Map<String, String> queryString = new HashMap<>();
        queryString.put("k", conditions.get("k") == null ? "" : conditions.get("k"));
        queryString.put("page", conditions.get("page") == null ? "" : conditions.get("page"));
        queryString.put("market_date", conditions.get("market_date") == null ? "" : conditions.get("market_date"));

        //不固定字段
        String[] keys = {
                "brand_name", "family_name", "engine_desc", "gearbox_name", "vehicle_fgw_code"
        };
        for (String key : keys) {
            if (conditions.get(key) != null && !conditions.get(key).equals("")) {
                queryString.put(key, conditions.get(key));
            }
        }
        JsonNode jsonNode;
        //当请求参数为3个时需要发送第一个请求,否则直接发第二个请求
        if (queryString.size() == 3) {
            jsonNode = sendFirstRequest(queryString);
            //判断第一个请求是否查到车型 0 成功 1 失败
            if (JacksonUtil.getIntegerNodeByKey(jsonNode, "code") != 0) {
                logger.info("按照条件未查询到车型列表");
                vehicleInfo.put("code", "1");
                return vehicleInfo;
            }
            //第一个请求查询是默认会添加brand_name
            queryString.put("brand_name", JacksonUtil.getMapNodeByKey(jsonNode, "selected").get("brand_name"));
        }
        jsonNode = sendSecondRequest(queryString);
        //判断第二个请求是否查到车型 0 成功 1 失败
        if (JacksonUtil.getIntegerNodeByKey(jsonNode, "code") != 0) {
            logger.info("按照条件未查询到车型列表");
            vehicleInfo.put("code", "1");
            return vehicleInfo;
        }
        vehicleInfo.put("code", "0");
        //车型列表
        vehicleInfo.put("vehicles", JacksonUtil.getListNodeByKey(jsonNode, "vehicles"));
        //车型筛选条件
        vehicleInfo.put("distincts", JacksonUtil.getMapNodeByKey(jsonNode, "distincts"));
        //车型分页信息
        vehicleInfo.put("pagination", JacksonUtil.getMapNodeByKey(jsonNode, "pagination"));
        return vehicleInfo;
    }

    /**
     * 平安查询车型接口一
     *
     * @param queryString 查询条件
     * @return 代表请求结果的JsonNode对象, 其中主要关心的节点有vehicles, selected, distincts等节点
     * @throws IOException
     */
    private static JsonNode sendFirstRequest(Map<String, String> queryString) throws Exception {
        return JacksonUtil.getJsonNode(BusinessUtil.executeGetMethod(_FIRST_REQUEST_URL, queryString));
    }

    /**
     * 平安查询车型接口二
     *
     * @param queryString 查询条件
     * @return 代表请求结果的JsonNode对象, 其中主要关心的节点有vehicles, selected, distincts等节点
     * @throws IOException
     */
    private static JsonNode sendSecondRequest(Map<String, String> queryString) throws Exception {
        return JacksonUtil.getJsonNode(BusinessUtil.executeGetMethod(_SECOND_REQUEST_URL, queryString));
    }

}

