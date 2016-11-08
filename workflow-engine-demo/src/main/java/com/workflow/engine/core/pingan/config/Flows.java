package com.workflow.engine.core.pingan.config;

import com.workflow.engine.core.common.FlowBuilder;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.flow.impl.Flow;
import com.workflow.engine.core.common.utils.ConfigUtils;
import com.workflow.engine.core.common.utils.FlowBuilderUtil;
import com.workflow.engine.core.pingan.steps.CreateQuote;
import com.workflow.engine.core.pingan.steps.GetVehicleModel;
//import com.keeper.www.pingan.steps.GetVehicleModelType1;
//import com.keeper.www.pingan.steps.GetVehicleModelType2;
//import com.keeper.www.pingan.steps.SaveQuoteInfo;
//import com.keeper.www.pingan.steps.BizQuote;
//import com.keeper.www.pingan.steps.CheckVehiclePriceHasTax;
//import com.keeper.www.pingan.steps.ForceQuote;
//import com.keeper.www.pingan.steps.RenewalCheckAndGetFlowId;

import com.workflow.engine.core.pingan.steps.n.SaveQuoteInfo;
import com.workflow.engine.core.pingan.steps.n.GetVehicleModelType1;
import com.workflow.engine.core.pingan.steps.n.GetVehicleModelType2;
import com.workflow.engine.core.pingan.steps.n.BizQuote;
import com.workflow.engine.core.pingan.steps.n.CheckVehiclePriceHasTax;
import com.workflow.engine.core.pingan.steps.n.ForceQuote;
import com.workflow.engine.core.pingan.steps.n.RenewalCheckAndGetFlowId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 流程与步骤配置
 * 可以配置子流程,并用子流程拼装成流程链
 * Created by houjinxin on 16/3/9.
 */
public class Flows {

    private static final String _FLOW_FILE_PATH = ConfigUtils.getStringByKey("pinganFlow");

    public static final Map<String, Class<? extends IStep>> _NAME_STEP_CLAZZ_MAPPINGS = new HashMap<String, Class<? extends IStep>>() {{
        put("续保检查", RenewalCheckAndGetFlowId.class);
        put("保存报价信息", SaveQuoteInfo.class);
        put("商业险报价", BizQuote.class);
        put("交强险报价", ForceQuote.class);
        put("构造Quote对象", CreateQuote.class);
        put("查询车型", GetVehicleModel.class);
        put("查询车型接口一", GetVehicleModelType1.class);
        put("查询车型接口二", GetVehicleModelType2.class);
        put("确定车价是否含税", CheckVehiclePriceHasTax.class);
    }};

    public static final LinkedList<Object> _QUOTE_FLOW_TYPE1_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "quote");

    public static final Flow _QUOTE_FLOW_TYPE1 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _QUOTE_FLOW_TYPE1_STEPS).build();

    public static final LinkedList<Object> _FIND_VEHICLE_TYPE1_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "findVehicle");

    public static final Flow _FIND_VEHICLE_FLOW_TYPE1 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _FIND_VEHICLE_TYPE1_STEPS).build();

}
