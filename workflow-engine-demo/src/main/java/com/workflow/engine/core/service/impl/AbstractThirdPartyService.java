package com.workflow.engine.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.workflow.engine.core.common.utils.DateUtils;
import com.workflow.engine.core.common.utils.FlowUtil;
import com.workflow.engine.core.service.IThirdPartyService;
import com.workflow.engine.core.service.entity.vo.ApplicantVO;
import com.workflow.engine.core.service.entity.vo.AreaVO;
import com.workflow.engine.core.service.entity.vo.AutoVO;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.service.entity.vo.WangXiaoVO;
import com.workflow.engine.core.common.Constants;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IFlow;
import com.workflow.engine.core.common.misc.ILogger;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.strategy.StrategyRegister._PARAMS_GENERATOR_STRATEGY;
import static com.workflow.engine.core.common.utils.BusinessUtil.createFailQuote;
import static com.workflow.engine.core.common.utils.BusinessUtil.createQuoteByStepInfo;
import static com.workflow.engine.core.common.utils.ExceptionUtil.getExceptionStackTrace;

/**
 * 模板方法实现
 * Created by houjinxin on 16/3/13.
 */
public abstract class AbstractThirdPartyService implements IThirdPartyService, ILogger {

    protected abstract Map<String, Object> getBaseContext();

    /**
     * 根据业务需要来创建不同的上下文
     *
     * @param bizObject 业务对象放到上下文中被整个流程共享
     * @return
     */
    private Map<String, Object> createContext(Object bizObject) {
        Map<String, Object> baseContext = getBaseContext();
        Map<String, Object> bizContext = createBizContext(bizObject);
        Map<String, Object> context = new HashMap<String, Object>();
        context.putAll(baseContext); //基础上下文
        context.putAll(bizContext); //业务上下文
        return context;
    }

    /**
     * 业务相关上下文,拆解业务对象内容,各家保险公司所需要的信息基本一致
     *
     * @param bizObject
     * @return
     */
    protected Map<String, Object> createBizContext(Object bizObject) {
        Map<String, Object> bizContext = new HashMap<String, Object>();
        //参数生成策略注册
        bizContext.put("paramsGeneratorStrategy", _PARAMS_GENERATOR_STRATEGY);
        bizContext.put("serviceName", this.getClass().getSimpleName());
        if (bizObject instanceof WangXiaoVO) {
            bizContext.put("flowType", "quote"); //添加一个表示流程类型的属性,用于在流程异常时获取对应流程类型的异常结果
            WangXiaoVO nwx = (WangXiaoVO) bizObject;
            ApplicantVO applicant = nwx.getApplicant(); //投保人信息
            AutoVO auto = nwx.getAuto(); //行驶证信息
            getLogger().info("投保车辆信息如下:\n{}", auto);
            AreaVO area = auto.getArea(); //汽车所在区域
            String cityAreaCode = nwx.getCityCode();
            getLogger().info("投保车辆投保的区域为:{}", cityAreaCode);
            InsurancePackageVO insurancePackage = nwx.getInsurancePackage(); //套餐信息
            if (applicant != null) {
                bizContext.put("applicantName", applicant.getApplicantName()); //投保人姓名
                bizContext.put("applicantPhone", applicant.getApplicantPhone()); //投保人手机
                bizContext.put("applicantEmail", applicant.getApplicantEmail()); //投保人邮箱
            }
            bizContext.put("licenseNo", auto.getLicenseNo()); //车牌号
            bizContext.put("engineNo", auto.getEngineNo()); //发动机号
            bizContext.put("carOwner", auto.getCarOwner()); //车主姓名
            bizContext.put("frameNo", auto.getFrameNo()); //车架号
            bizContext.put("identity", auto.getIdentity()); //身份证号
            bizContext.put("enrollDate", auto.getEnrollDate() != null ? DateUtils.format(auto.getEnrollDate(), "yyyy-MM-dd") : ""); //初登日期
            bizContext.put("issuingDate", auto.getIssuingDate()!= null ? DateUtils.format(auto.getIssuingDate(), "yyyy-MM-dd") : ""); //发证日期
            bizContext.put("brandCode", auto.getBrandCode()); //品牌型号 用于查询车型
            bizContext.put("startDate", DateUtils.addDay(1, "yyyy-MM-dd"));
            bizContext.put("endDate", DateUtils.addYear(1, "yyyy-MM-dd"));
            bizContext.put("markID", nwx.getMarkID());//查询车型选中车辆标识
            bizContext.put("bizCanApply", true); //默认商业险可投保,在商业险报价环节可能会被修改
            bizContext.put("forceCanApply", true); //默认交强险可投保,在交强险报价环节可能会被修改

            bizContext.put("area", area); //城市信息,包含cityCode, cityName等
            bizContext.put("cityAreaCode", cityAreaCode); //只包含cityCode
            bizContext.put("cityIsBeijing", "110100".equals(cityAreaCode)); //判断当前城市是否是北京
            bizContext.put("insurancePackage", insurancePackage);
            //下面几个条件备用
            bizContext.put("bizStartDate", DateUtils.addDay(1, "yyyy-MM-dd"));
            bizContext.put("bizEndDate", DateUtils.addYear(1, "yyyy-MM-dd"));
            bizContext.put("forceStartDate", DateUtils.addDay(1, "yyyy-MM-dd"));
            bizContext.put("forceEndDate", DateUtils.addYear(1, "yyyy-MM-dd"));
        } else { //处理其他业务对象如查找车型
            bizContext.put("flowType", "findVehicle");
        }
        return bizContext;
    }

    /**
     * 用业务对象来提供报价流程中需要的数据,流程结束时返回一个期望的结果
     *
     * @param bizObject 业务对象
     * @return 期望类型的对象
     */
    @Override
    public final Object quote(Object bizObject) {
        Map<String, Object> context = createContext(bizObject);
        IFlow flow = FlowUtil.getQuoteFlow(context);
        return service(context, flow);
    }

    @Override
    public final Object findVehicle(Object bizObject) {
        Map<String, Object> context = createContext(bizObject);
        IFlow flow = FlowUtil.getFindVehicleFlow(context);
        return service(context, flow);
    }

    /**
     * 各类流程启动,将最终的处理结果赋值给业务对象
     *
     * @param context 上下文
     * @param flow    流程
     * @return 期望类型的目标对象
     */
    private Object service(Map<String, Object> context, IFlow flow) {
        Object obj = null;
        try {
            long beginTime = System.currentTimeMillis();
            StepState flowState = flow.run(context);
            long endTime = System.currentTimeMillis();
            getLogger().info("调用接口所需时间：{}s", ((endTime - beginTime) / 1000));
            if (flowState.getStatusFlag() == Constants._STEP_SUCCESS_FLAG) {
                obj = getTargetBizObject(context);
            } else if (flowState.getStatusFlag() == Constants._STEP_FAILURE_FLAG) {
                obj = getFailureTargetBizObject(context, flowState.getMsgEnum());
            } else if (flowState.getStatusFlag() == Constants._STEP_SUCCESS_WITH_INFO_FLAG) {
                obj = getStepInfoObject(context , flowState.getStepInfo() , flowState.getMsgEnum());
            }
        } catch (Exception e) { //针对不同场景定义一些业务异常,在适当时候抛出业务异常
            e.printStackTrace();
            getLogger().error("异常描述如下:\n{}", e);
            obj = createFailQuote((String)context.get("serviceName"), MsgEnum.Other);
//            return obj;
            //if(e instanceOf BusinessException)
            //非特定业务异常的其他情况转为业务异常抛出
            //throw new BusinessException("")
        }
        return obj;
    }

    /**
     * 流程正确运行返回的结果
     *
     * @param context
     * @return
     */
    private Object getTargetBizObject(Map<String, Object> context) {
        String flowType = (String) context.get("flowType");
        Object targetBizObject = context.get("result");
        getLogger().info("{}[ {} ]流程执行成功,目标业务对象为:\n{}", this.getClass().getSimpleName(), flowType, JSON.toJSON(targetBizObject));
        return targetBizObject;
    }

    /**
     * 流程异常时,根据不同类型流程返回相应的异常结果
     *
     * @param context
     * @param msgEnum
     * @return
     */
    private Object getFailureTargetBizObject(Map<String, Object> context, MsgEnum msgEnum) {
        String flowType = (String) context.get("flowType");
        Object targetBizObject;
        if (flowType.equals("quote")) {
            targetBizObject = createFailQuote((String) context.get("serviceName"), msgEnum);
        } else {
            targetBizObject = null;
        }
        getLogger().info("{}[ {} ]流程失败, 返回失败目标业务对象为:\n{}", this.getClass().getSimpleName(), flowType, JSON.toJSON(targetBizObject));
        return targetBizObject;
    }

    /**
     * 用来返回流程中某些步骤的返回值
     *
     * @param context
     * @param stepInfo 
     * @param msgEnum
     * @return
     */
    private Object getStepInfoObject(Map<String, Object> context, Object stepInfo, MsgEnum msgEnum) {
        Object targetStepInfoObject;
        if(MsgEnum.QueryCar_VehicleInformation.equals(msgEnum)){
            targetStepInfoObject = createQuoteByStepInfo((String) context.get("serviceName"), msgEnum, stepInfo);
        } else {
            targetStepInfoObject = null;
        }
        return targetStepInfoObject;
    }

}
