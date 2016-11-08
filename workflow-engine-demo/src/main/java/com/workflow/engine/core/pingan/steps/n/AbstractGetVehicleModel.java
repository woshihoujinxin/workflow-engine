package com.workflow.engine.core.pingan.steps.n;

import com.workflow.engine.core.common.StepState;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executeGetMethod;
import static com.workflow.engine.core.common.utils.JacksonUtil.getIntegerNodeByKey;
import static com.workflow.engine.core.common.utils.JacksonUtil.getJsonNode;
import static com.workflow.engine.core.common.utils.JacksonUtil.getJsonNodeByKeyAndType;
import static com.workflow.engine.core.common.utils.JacksonUtil.getMapNodeByKey;

/**
 * 车型查询抽象基类
 * Created by houjinxin on 16/4/28.
 */
public abstract class AbstractGetVehicleModel extends PinganAbstractStep {

    /**
     * 车型查找不到时,可以做些处理,并返回一个失败的步骤状态,并且要指明失败原因
     *
     * @return 步骤状态
     */
    protected abstract StepState vehiclesNotFound();

    /**
     * 车型查找成功时,不同步骤根据具体情况,走不同的分支,可以通过这个方法来实现
     *
     * @return
     */
    protected abstract StepState vehiclesFound();

    /**
     * 处理响应:
     * 1.在响应中拿到需要的信息
     * 2.如果车型查询成功,则将车薪信息放到上下文中,主要有车型列表(vehicles), 筛选条件(distincts), 分页信息(pagination), 品牌名称(brand_name)
     * 3.如果查询失败终止流程,并提示失败信息
     *
     * @param context
     * @param realResponse
     * @return
     */
    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = getJsonNode(realResponse);
        //判断第一个请求是否查到车型 0 成功 1 失败
        if (getIntegerNodeByKey(jsonNode, "code") == 1) {
            return vehiclesNotFound();
        } else {
            List<Map<String, String>> vehicles = getJsonNodeByKeyAndType(jsonNode, "vehicles", new TypeReference<List<Map<String, String>>>(){});
            Map<String, String> distincts = getMapNodeByKey(jsonNode, "distincts");
            Map<String, String> pagination = getMapNodeByKey(jsonNode, "pagination");
            String brand_name = getMapNodeByKey(jsonNode, "selected").get("brand_name");
            //车型列表
            context.put("vehicles", vehicles);
            //筛选条件
            context.put("distincts", distincts);
            //分页信息
            context.put("pagination", pagination);
            //接口二用到的参数
            context.put("brand_name", brand_name);

            getLogger().info("查找车型成功,车型列表如下:\n{}", vehicles);
            //TODO:现在只选择第一辆车
            Map<String, String> vehicle = vehicles.get(0);
            //用于判定车座数是否为0
            context.put("seatCountIsZero", vehicle.get("seat").equals("0"));
            context.put("vehicleInfo", vehicle);
            return vehiclesFound();
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return executeGetMethod(requestUrl, requestParams, headers);
    }

}
