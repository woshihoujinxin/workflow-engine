package com.workflow.engine.core.picc.config;

import com.workflow.engine.core.common.FlowBuilder;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.flow.impl.Flow;
import com.workflow.engine.core.common.utils.ConfigUtils;
import com.workflow.engine.core.common.utils.FlowBuilderUtil;
import com.workflow.engine.core.picc.steps.AjaxBlacklist;
import com.workflow.engine.core.picc.steps.AjaxPlateNumber;
import com.workflow.engine.core.picc.steps.BlackList;
import com.workflow.engine.core.picc.steps.CalculateFee;
import com.workflow.engine.core.picc.steps.CarOwner;
import com.workflow.engine.core.picc.steps.CheckStartDate;
import com.workflow.engine.core.picc.steps.CreateQuote;
import com.workflow.engine.core.picc.steps.GetEnrollDateScope;
import com.workflow.engine.core.picc.steps.IdentifyBlackList;
import com.workflow.engine.core.picc.steps.RandFrameNo;
import com.workflow.engine.core.picc.steps.VerifyCaptcha;
import com.workflow.engine.core.picc.steps.VerifyEngineNo;
import com.workflow.engine.core.picc.steps.n.CalculateFeeJQ;
import com.workflow.engine.core.picc.steps.n.CalculateFeeSY;
import com.workflow.engine.core.picc.steps.n.CarChecked;
import com.workflow.engine.core.picc.steps.n.CarDataReuseCheck;
import com.workflow.engine.core.picc.steps.n.CarQuery;
import com.workflow.engine.core.picc.steps.n.GetEnabledInsuranceItems;
import com.workflow.engine.core.picc.steps.n.GetInterimNo;
import com.workflow.engine.core.picc.steps.n.GetSessionId;
import com.workflow.engine.core.picc.steps.n.QueryCarDataReuse;
import com.workflow.engine.core.picc.steps.n.QueryRenewal;
import com.workflow.engine.core.picc.steps.n.RenewalCheck;
import com.workflow.engine.core.picc.steps.n.UnderwriteCheckProfitAjax;
import com.workflow.engine.core.picc.steps.n.VehicleChecked;
import com.workflow.engine.core.picc.steps.n.VehicleFind;
//import com.keeper.www.picc.steps.CalculateFeeJQ;
//import com.keeper.www.picc.steps.CalculateFeeSY;
//import com.keeper.www.picc.steps.CarChecked;
//import com.keeper.www.picc.steps.CarDataReuseCheck;
//import com.keeper.www.picc.steps.CarQuery;
//import com.keeper.www.picc.steps.GetEnabledInsuranceItems;
//import com.keeper.www.picc.steps.GetInterimNo;
//import com.keeper.www.picc.steps.GetSessionId;
//import com.keeper.www.picc.steps.QueryCarDataReuse;
//import com.keeper.www.picc.steps.QueryRenewal;
//import com.keeper.www.picc.steps.RenewalCheck;
//import com.keeper.www.picc.steps.UnderwriteCheckProfitAjax;
//import com.keeper.www.picc.steps.VehicleChecked;
//import com.keeper.www.picc.steps.VehicleFind;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 流程与步骤配置
 * 可以配置子流程,并用子流程拼装成流程链
 * Created by houjinxin on 16/3/9.
 */
public class Flows {

    private static final String _FLOW_FILE_PATH = ConfigUtils.getStringByKey("piccFlow");

    //    ajaxPlateNumber  验证车牌号
    //    isRenewal 判断是否续保
    //    carDataReuse 车型数据是否可复用（应该指曾在人保投保 车型有记录）
    //    carOwner 验证车主是否为空， 这步可省略
    //    CreateImage 获取图片
    //    verifyCodeCheck 识别验证码
    //    query_carDataReuse 查询车型信息
    //    carOwner 再次验证
    //    randFrameNo 车架号校验
    //    verifyEngineNo 发动机号校验
    //    carQuery 车型查询
    //    carChecked 车型校验
    //    getEnrollDateScope 初登日期
    //    checkStartDate 起保日期
    //    ajaxBlackList 黑名单验证
    //    checkStartDate 起保日期
    //    blackList 黑名单
    //    underwriteCheckProfitAjax
    //    interim 获取interim编号
    //    calculate 计算报价界面 可以获取可选套餐 套餐描述与请求参数的关系
    //    calculateFee/fee 计算商业险
    //    calculateFee/jq 计算交强险

    public static final Map<String, Class<? extends IStep>> _NAME_STEP_CLAZZ_MAPPINGS = new HashMap<String, Class<? extends IStep>>() {{
        put("获取SessionId", GetSessionId.class);
        put("检查车型数据是否可复用", CarDataReuseCheck.class);
        put("续保检查", RenewalCheck.class);
        put("查询续保信息", QueryRenewal.class);
        put("车型查询", CarQuery.class);
        put("车型查询二", VehicleFind.class);
        put("校验车型", CarChecked.class);
        put("校验车型二", VehicleChecked.class);
        put("获取初等日期范围", GetEnrollDateScope.class);
        put("检查起保日期", CheckStartDate.class);
        put("黑名单", BlackList.class);
        put("核保", UnderwriteCheckProfitAjax.class);
        put("商业险报价", CalculateFeeSY.class);
        put("交强险报价", CalculateFeeJQ.class);
        put("获取可选的险种", GetEnabledInsuranceItems.class);
        put("查询可复用的车型信息", QueryCarDataReuse.class);
        put("车主信息校验", CarOwner.class);
        put("车架号校验", RandFrameNo.class);
        put("发动机校验", VerifyEngineNo.class);
        put("获取InterimNo", GetInterimNo.class);
        put("识别验证码", VerifyCaptcha.class);
        put("校验车牌号", AjaxPlateNumber.class);
        put("Ajax黑名单", AjaxBlacklist.class);
        put("获取所有支持的险种", CalculateFee.class);
        put("构造报价结果", CreateQuote.class);
        put("被保险人证件号验证", IdentifyBlackList.class);
    }};

    public static final LinkedList<Object> _QUOTE_FLOW_TYPE1_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "type1");

    //流程1 支持北京
    public static final Flow _QUOTE_FLOW_TYPE1 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _QUOTE_FLOW_TYPE1_STEPS).build();

    public static final LinkedList<Object> _QUOTE_FLOW_TYPE2_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "type2");

    //流程2 支持天津,成都等城市
    public static final Flow _QUOTE_FLOW_TYPE2 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _QUOTE_FLOW_TYPE2_STEPS).build();

    public static final LinkedList<Object> _QUOTE_FLOW_TYPE3_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "type3");

    //流程3 支持成都等城市
    public static final Flow _QUOTE_FLOW_TYPE3 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _QUOTE_FLOW_TYPE3_STEPS).build();

//    public static final LinkedList<Object> _FLOW_TYPE1_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "flow_type1");
//
//    public static final Flow _FLOW_TYPE1 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _FLOW_TYPE1_STEPS).build();
//
//    public static final LinkedList<Object> _FLOW_TYPE2_STEPS = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH, "flow_type2");
//
//    public static final Flow _FLOW_TYPE2 = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _FLOW_TYPE2_STEPS).build();
//
//    public static final FlowChain _FLOW_CHAIN_TYPE1 = new FlowChain(new LinkedList<Flow>(){{
//        add(_FLOW_TYPE1);
//        add(_FLOW_TYPE2);
//    }});

}
