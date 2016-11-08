package com.workflow.engine.core.common.utils;


import com.workflow.engine.core.common.flow.IFlow;
import com.workflow.engine.core.service.entity.vo.AreaVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 流程操作用到的一些通用方法
 * Created by houjinxin on 16/3/11.
 */
public class FlowUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlowUtil.class);

    public static IFlow getQuoteFlow(Map<String, Object> context) {
        return checkCityFlow(context);
    }

    public static IFlow getFindVehicleFlow(Map<String, Object> context) {
        Map<String, IFlow> cityFlowMapping = (Map<String, IFlow>) context.get("cityFindVehicleFlowMapping");
        return cityFlowMapping.get("default");
    }

    private static IFlow checkCityFlow(Map<String, Object> context) {
        AreaVO area = (AreaVO) context.get("area");
        String name = area.getName();
        String cityCode = area.getCityCode();
        Map<String, IFlow> cityFlowMapping = (Map<String, IFlow>) context.get("cityQuoteFlowMapping");
        IFlow flow = cityFlowMapping.get(cityCode);
        if (flow == null) {
            logger.info("因FlowMappings中没有配置[ " + name + " ]对应流程实例,故选择默认流程实例,请确定是否需要配置[ " + cityCode + " ]对应的流程实例");
            flow = cityFlowMapping.get("default");
        }
        if (flow == null) {
            throw new RuntimeException("FlowMappings中没有配置[ default ]对应流程实例");
        }
        return flow;
    }


}
