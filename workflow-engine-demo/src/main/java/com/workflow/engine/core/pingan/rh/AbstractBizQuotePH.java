package com.workflow.engine.core.pingan.rh;

import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.misc.ILogger;
import com.workflow.engine.core.common.rh.IParamsHandler;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executeReadMethod;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;

/**
 * 报价请求的ParamsHandler的抽象实现,实现了BizQuote步骤构造报价请求的公共逻辑,
 * 子类只需要实现getItemsMappings()方法提供映射关系即可
 * Created by houjinxin on 16/4/8.
 */
public abstract class AbstractBizQuotePH implements IParamsHandler, ILogger {

    abstract protected String[][] getItemsMappings();

    @Override
    public Map<String, String> generateParams(Map<String, Object> context) throws Exception {
        return generateParams(context, null);
    }

    @Override
    public Map<String, String> generateParams(Map<String, Object> context, Object... others) throws Exception {
        String flowId = (String) context.get("flowId");
        String xrc = (String) context.get("xrc");
        String bizBeginDate = (String) context.get("bizBeginDate");
        String forceBeginDate = (String) context.get("forceBeginDate");
        String inputAmount = (String) context.get("inputAmount");

        Map<String, String> requestParams = new LinkedHashMap<>();
        requestParams.put("flowId", flowId);
        requestParams.put("__xrc", xrc);
        requestParams.put("responseProtocol", "json");
        requestParams.put("bizInfo.isNeedRuleCheck", "false");
        requestParams.put("bizConfig.pkgName", "optional");
        requestParams.put("bizInfo.beginDate", bizBeginDate);
        requestParams.put("forceInfo.beginDate", forceBeginDate);
        requestParams.put("bizConfig.inputAmount", inputAmount);
        convertInsurancePackage(context, requestParams);
        return requestParams;
    }

    protected void convertInsurancePackage(Map<String, Object> context, Map<String, String> otherRequestParams) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        Map<String, String> kindCode2Amount = new HashMap<String, String>();
        InsurancePackageVO ip = (InsurancePackageVO) context.get("insurancePackage");
        String[][] itemsMappings = getItemsMappings();
        getLogger().info("当前报价套餐为:\n{}", ip);
        //可选套餐
        List<Map<String, String>> enabledInsuranceList = (List<Map<String, String>>) context.get("enabledInsuranceList");
        Map<String, String> textToParams = collect(enabledInsuranceList, "name", "text");
        for (String[] mapping : itemsMappings) {
            String cnName = mapping[5];
            String requestParamName = mapping[4];
            String propName = mapping[6];
            String handleType = mapping[1];
            if (handleType.equals("pType1")) { //double2double
                otherRequestParams.put(requestParamName, amountConvertFromIPDouble(ip, propName));
            } else if (handleType.equals("pType2")) { //fromResponse
                otherRequestParams.put(requestParamName, amountConvertFromResponse(context, propName));
            } else if (handleType.equals("pType3")) { //booleanTo1or0
                otherRequestParams.put(requestParamName, amountConvertFromIPBoolean(ip, propName));
            } else if (handleType.equals("pType4")) { //glass, specialFactory
                otherRequestParams.put(requestParamName, amountConvertFromIPString(ip, propName));
            } else { //目前没有第五种参数类型

            }
            //根据上一个步骤中确定所有的可选套餐,若可选套餐中不在映射关系中,那么应该取消该险种
            if(textToParams.get(requestParamName) == null) {
                otherRequestParams.put(requestParamName, "0");
            }
            //保存各险种的保额
            kindCode2Amount.put(requestParamName, otherRequestParams.get(requestParamName));
            getLogger().info("正在处理{},其保额的对应关系为{}={}", cnName, requestParamName, otherRequestParams.get(requestParamName));
        }
        context.put("kindCode2Amount", kindCode2Amount);
    }

    /**
     * 转换来自套餐中的Double型险种的保额. 如三者, 司机, 乘客, 划痕.
     *
     * @param ip       套餐对象
     * @param propName InsurancePackage中的属性名
     * @return 该险种的在报价请求参数中的值
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String amountConvertFromIPDouble(InsurancePackageVO ip, String propName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        double amount = (double) executeReadMethod(propName, ip);
        if (amount % 1 == 0) { //amount为整数, 小数点后面为0
            return String.valueOf((int) amount);
        } else { //amount不是整数，小数点后面不是0
            return String.valueOf(amount);
        }
    }

    /**
     * 处理来自之前的步骤响应中的某些险种的保额. 如车损, 盗抢和自燃. 这些值将会存储在上下文中, 约定按照InsurancePackage中的字段名称做为key
     *
     * @param context  上下文
     * @param propName InsurancePackage中的属性名
     * @return 该险种的在报价请求参数中的值
     */
    private String amountConvertFromResponse(Map<String, Object> context, String propName) {
        return (String) context.get(propName);
    }

    /**
     * 处理套餐中boolean类型险种对应的请求, 如各种不计免赔, 以及盗抢,车损和自燃,选中为1,不选为-0
     *
     * @param ip       套餐对象
     * @param propName InsurancePackage中的属性名
     * @return 该险种的在报价请求参数中的值
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String amountConvertFromIPBoolean(InsurancePackageVO ip, String propName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        return (Boolean) executeReadMethod(propName, ip) ? "1" : "0";
    }

    /**
     * 从套餐中获取玻璃险,指定专修长的选项
     * glass,specialFactory 0-不投保 1-国产 2-进口, 对应平安的0-不投保, 1-国产, 2-进口
     *
     * @param ip
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String amountConvertFromIPString(InsurancePackageVO ip, String propName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        String value = executeReadMethod(propName, ip).toString();
        return value.equals("1") ? "1" : value.equals("2") ? "2" : "0";
    }
}
