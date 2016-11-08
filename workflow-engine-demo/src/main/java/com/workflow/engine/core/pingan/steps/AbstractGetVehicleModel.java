package com.workflow.engine.core.pingan.steps;

import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.misc.ILogger;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.pingan.utils.PinganBizUtil;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 车型查询抽象基类
 * Created by houjinxin on 16/4/28.
 */
public abstract class AbstractGetVehicleModel implements IStep, ILogger {

    protected abstract String getApiPath();

    protected abstract Map<String, String> getRequestParams(Map<String, Object> context) throws Exception;

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
     * @param response
     * @return
     */
    protected StepState handleResponse(Map<String, Object> context, Object response) throws IOException {
        JsonNode jsonNode = JacksonUtil.getJsonNode((String) response);
        //判断第一个请求是否查到车型 0 成功 1 失败
        if (JacksonUtil.getIntegerNodeByKey(jsonNode, "code") == 1) {
            return vehiclesNotFound();
        } else {
            List<Map<String, String>> vehicles = JacksonUtil.getJsonNodeByKeyAndType(jsonNode, "vehicles", new TypeReference<List<Map<String, String>>>(){});
            Map<String, String> distincts = JacksonUtil.getMapNodeByKey(jsonNode, "distincts");
            Map<String, String> pagination = JacksonUtil.getMapNodeByKey(jsonNode, "pagination");
            String brand_name = JacksonUtil.getMapNodeByKey(jsonNode, "selected").get("brand_name");
            //车型列表
            context.put("vehicles", vehicles);
            //筛选条件
            context.put("distincts", distincts);
            //分页信息
            context.put("pagination", pagination);
            //接口二用到的参数
            context.put("brand_name", brand_name);
            //TODO:现在只选择第一辆车
            context.put("vehicleInfo", vehicles.get(0));
            getLogger().info("查找车型成功,车型列表如下:\n{}", vehicles);
            return vehiclesFound();
        }
    }

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executeGetMethod(getApiPath(), requestParams, PinganBizUtil.getHeaders());
        return handleResponse(context, response);
    }

}
