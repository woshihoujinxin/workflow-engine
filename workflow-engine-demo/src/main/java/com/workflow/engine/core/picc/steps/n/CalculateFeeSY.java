package com.workflow.engine.core.picc.steps.n;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.annotation.PGConfig;
import com.workflow.engine.core.common.annotation.PGScheme;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.utils.JacksonUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.executeReadMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;
import static com.workflow.engine.core.common.utils.JacksonUtil.getListNodeByKey;
import static com.workflow.engine.core.common.utils.JacksonUtil.getStringNodeByKey;
import static com.workflow.engine.core.picc.config.Constants._KIND_CODE_MAPPINGS;

/**
 * 商业险报价
 * Created by houjinxin on 16/3/9.
 */
@PGConfig({
        @PGScheme(requestParamName = "channelNo", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "sessionId", strategy = StrategyType.CONTEXT, strategyNeedParams = {"sessionId", ""}),
        @PGScheme(requestParamName = "proSelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "citySelected", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "areaCodeLast", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.areaCode", "11000000"}),
        @PGScheme(requestParamName = "cityCodeLast", strategy = StrategyType.CONTEXT, strategyNeedParams = {"cityCodeMapping.{cityAreaCode}.cityCode", "11000000"}),
        @PGScheme(requestParamName = "mobile", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.insuredInfo.appliEmail", "18911111111"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"", "18911111111"}),
        @PGScheme(requestParamName = "email", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.insuredInfo.appliMobile", "121@qq.com"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"", "121@qq.com"}),
        @PGScheme(requestParamName = "identifytype", strategy = StrategyType.FIXED, strategyNeedParams = "01"),
        @PGScheme(requestParamName = "identifynumber", strategy = StrategyType.CONTEXT, strategyNeedParams = {"identity", ""}),
        @PGScheme(requestParamName = "birthday", strategy = StrategyType.METHOD, strategyNeedParams = "$getBirthdayFromIdentity(identity, \"yyyy/MM/dd\")"),
        @PGScheme(requestParamName = "sex", strategy = StrategyType.METHOD, strategyNeedParams = "$getGenderFromIdentity(identity, \"1\",\"2\")"),
        @PGScheme(requestParamName = "startdate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"startDate", ""}),
        @PGScheme(requestParamName = "starthour", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "enddate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"endDate", ""}),
        @PGScheme(requestParamName = "endhour", strategy = StrategyType.FIXED, strategyNeedParams = "24"),
        @PGScheme(requestParamName = "isRenewal", strategy = StrategyType.CONTEXT, strategyNeedParams = {"isRenewal", ""}),
        @PGScheme(requestParamName = "licenseno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"licenseNo", ""}),
        @PGScheme(requestParamName = "nonlocalflag", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "licenseflag", strategy = StrategyType.FIXED, strategyNeedParams = "1"),
        @PGScheme(requestParamName = "engineno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"engineNo", ""}),
        @PGScheme(requestParamName = "vinno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "frameno", strategy = StrategyType.CONTEXT, strategyNeedParams = {"frameNo", ""}),
        @PGScheme(requestParamName = "newcarflag", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "isOutRenewal", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050200", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050210", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "lastHas050500", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "seatCount", valueSourceExpression = "$val(context, \"reuseCarData\")", strategyIfTrue = StrategyType.CONTEXT, strategyNeedParamsIfTrue = {"vehicleInfo.appliCarInfo.seat", "121@qq.com"}, strategyIfFalse = StrategyType.CONTEXT, strategyNeedParamsIfFalse = {"vehicleInfo.seat", "121@qq.com"}),
        @PGScheme(requestParamName = "seatflag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "beforeProposalNo", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "enrolldate", strategy = StrategyType.CONTEXT, strategyNeedParams = {"enrollDate", ""}),
        @PGScheme(requestParamName = "transfervehicleflag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "insuredname", strategy = StrategyType.CONTEXT, strategyNeedParams = {"carOwner", ""}),
        @PGScheme(requestParamName = "fullAmountName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "guohuselect", strategy = StrategyType.FIXED, strategyNeedParams = "0"),
        @PGScheme(requestParamName = "runAreaCodeName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "assignDriver", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "haveLoan", strategy = StrategyType.FIXED, strategyNeedParams = "2"),
        @PGScheme(requestParamName = "LoanName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "weiFaName", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "carDrivers", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "transferdate", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "travelMilesvalue", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "lastdamageBI", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "noDamyearsBI", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaFlag", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaID", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "ccaEntryId", strategy = StrategyType.FIXED, strategyNeedParams = ""),
        @PGScheme(requestParamName = "BZ_selected", strategy = StrategyType.FIXED, strategyNeedParams = "2")
})
public class CalculateFeeSY extends PiccAbstractStep {

    private static final Logger logger = LoggerFactory.getLogger(CalculateFeeSY.class);

    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = super.getHeaders();
        headers.put("Origin", "http://www.epicc.com.cn");
        headers.put("Referer", "http://www.epicc.com.cn/wap/carProposal/car/calculateFee");
        return headers;
    }

    @Override
    protected String getRequestUrl() {
        return "http://www.epicc.com.cn/wap/carProposal/calculateFee/sy";
    }

    @Override
    public Map<String, String> getRequestParams(Map<String, Object> context, IStep step) throws Exception {
        Map<String, String> requestParams = super.getRequestParams(context, step);
        convertInsurancePackage(context, requestParams);
        return requestParams;
    }

    @Override
    protected Object sendRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return executePostMethod(requestUrl, requestParams, headers);
    }

    @Override
    protected StepState doHandleJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(realResponse));
        String resultCode = getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            getItemsPremium(jsonNode, context);
            //TODO:商业险起保日期获取的逻辑也要经过测试才能验证
            context.put("bizBeginDate", context.get("startDate"));
            return successfulStepState(this);
        } else {
            logger.info("商业险报价成功,响应为: \n{}", realResponse);
            return failureStepState(this);
        }
    }

    @Override
    protected StepState doHandleNotJsonResponse(Map<String, Object> context, String realResponse) throws Exception {
        return null;
    }

    /**
     * 获取各险种报价保额
     *
     * @param jsonNode 响应JSON
     */
    public void getItemsPremium(JsonNode jsonNode, Map<String, Object> context) throws IOException {
        List<Map<String, String>> items = getListNodeByKey(jsonNode, "items");
        List<Map<String, String>> kindCode2Premiums = collect(items, new String[]{"kindCode", "premium"});

        Map<String, String> kindCode2Premium = collect(kindCode2Premiums, "kindCode", "premium");
        List<Map<String, String>> basicPackage = getListNodeByKey(jsonNode, "basicPackage");
        Map<String, String> firstMap = basicPackage.get(0);
        kindCode2Premium.put("total", firstMap.get("premium")); //各险种报价总和
        logger.info("报价成功,报价结果为:\n{}", kindCode2Premium);
        context.put("kindCode2Premium", kindCode2Premium);
    }

    /**
     * 针对不同的参数来源,分类做不同的处理,并且将各个险种的保额收集起来放到上下文中,
     * 处理过程中要注意参数的顺序, 分别给出正确顺序和错误顺序的示例.
     * 如下所示:
     * 1.正确顺序
     * otherRequestParams.put("select_050200", "388000.00");
     * otherRequestParams.put("select_050600", "50000");
     * otherRequestParams.put("select_050500", "329800.00");
     * otherRequestParams.put("select_050701", "-1");
     * otherRequestParams.put("select_050702", "-1");
     * otherRequestParams.put("select_050310", "329800.00");
     * otherRequestParams.put("select_050231", "-1");
     * otherRequestParams.put("select_050270", "");
     * otherRequestParams.put("select_050210", "2000");
     * otherRequestParams.put("select_050252", "");
     * otherRequestParams.put("select_050291", "1");
     * otherRequestParams.put("select_050911", "1");
     * otherRequestParams.put("select_050912", "1");
     * otherRequestParams.put("select_050921", "-1");
     * otherRequestParams.put("select_050922", "-1");
     * otherRequestParams.put("select_050924", "-1");
     * otherRequestParams.put("select_050928", "-1");
     * otherRequestParams.put("select_050330", "");
     * otherRequestParams.put("select_050935", "");
     * otherRequestParams.put("select_050918", "");
     * otherRequestParams.put("select_050919", "");
     * otherRequestParams.put("select_050917", "");
     * otherRequestParams.put("select_050451", "");
     * otherRequestParams.put("select_050642", "");
     * otherRequestParams.put("select_050641", "");
     * otherRequestParams.put("select_050643", "");
     * otherRequestParams.put("select_050929", "-1");
     * 2.错误顺序
     * otherRequestParams.put("select_050200", "388000.00");
     * otherRequestParams.put("select_050270", "");
     * otherRequestParams.put("select_050252", "");
     * otherRequestParams.put("select_050330", "");
     * otherRequestParams.put("select_050935", "");
     * otherRequestParams.put("select_050918", "-1"); //
     * otherRequestParams.put("select_050919", "");
     * otherRequestParams.put("select_050917", "");
     * otherRequestParams.put("select_050451", "");
     * otherRequestParams.put("select_050642", "-1"); //
     * otherRequestParams.put("select_050641", "");
     * otherRequestParams.put("select_050643", "");
     * otherRequestParams.put("select_050600", "50000.0"); //
     * otherRequestParams.put("select_050500", "329800.00");
     * otherRequestParams.put("select_050701", "-1");
     * otherRequestParams.put("select_050702", "-1");
     * otherRequestParams.put("select_050310", "329800.00");
     * otherRequestParams.put("select_050231", "-1");
     * otherRequestParams.put("select_050210", "2000");
     * otherRequestParams.put("select_050291", "1");
     * otherRequestParams.put("select_050911", "1");
     * otherRequestParams.put("select_050912", "1");
     * otherRequestParams.put("select_050921", "-1");
     * otherRequestParams.put("select_050922", "-1");
     * otherRequestParams.put("select_050924", "-1");
     * otherRequestParams.put("select_050928", "-1");
     * otherRequestParams.put("select_050929", "-1");
     *
     * @param context
     * @param otherRequestParams
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public void convertInsurancePackage(Map<String, Object> context, Map<String, String> otherRequestParams) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        Map<String, String> kindCode2Amount = new HashMap<String, String>();
        InsurancePackageVO ip = (InsurancePackageVO) context.get("insurancePackage");
        logger.info("当前报价套餐为:\n{}", ip);
        for (String[] mapping : _KIND_CODE_MAPPINGS) {
            String itemName = mapping[2];
            String requestParamName = "select_" + mapping[1];
            String propName = mapping[3];
            String handleType = mapping[0];
            if (handleType.equals("type1")) { //double2double
                otherRequestParams.put(requestParamName, amountConvertFromIPDouble(ip, propName));
            } else if (handleType.equals("type2")) { //fromResponse
                otherRequestParams.put(requestParamName, amountConvertFromResponse(context, propName));
            } else if (handleType.equals("type3")) { //booleanTo1or0
                otherRequestParams.put(requestParamName, amountConvertFromIPBoolean(ip, propName));
            } else if (handleType.equals("type4")) { //glass
                otherRequestParams.put(requestParamName, glassConvertFromIPBoolean(ip));
            } else if (handleType.equals("type5")) { //none
                otherRequestParams.put(requestParamName, unsupportItem());
            } else if (handleType.equals("type6")) { //specialFactory
                otherRequestParams.put(requestParamName, specialFactoryConvertFromIPString(ip));
            }
            //保存各险种的保额
            kindCode2Amount.put(mapping[1], otherRequestParams.get(requestParamName) == null ? "" : otherRequestParams.get(requestParamName));
            logger.info("正在处理{},其保额的对应关系为{}={}", itemName, requestParamName, otherRequestParams.get(requestParamName));
        }
        //不可选险种对应的请求参数都是空所以在这里要对相应的参数做一次修改
        List<Map<String, String>> disabledInsuranceList = (List<Map<String, String>>) context.get("disabledInsuranceList");
        if (disabledInsuranceList != null) {
            for (Map<String, String> kindCodeMap : disabledInsuranceList) {
                String kindCode = kindCodeMap.get("kindCode");
                String requestParamName = "select_" + kindCode;
                otherRequestParams.put(requestParamName, disabledItem());
                kindCode2Amount.put(kindCode, otherRequestParams.get(kindCode));
                //保存各险种的保额
                logger.info("修改不可选险种{}的保额为{}", requestParamName, otherRequestParams.get(requestParamName));
            }
            context.put("kindCode2Amount", kindCode2Amount);
        }
    }

    /**
     * 转换来自套餐中的Double型险种的保额. 如三者, 司机, 乘客, 划痕. 这几个险种在请求中必须用没有不含".0"的整形的字符串表示.
     * otherRequestParams.put("select_050600", "2000000"); //三者
     * otherRequestParams.put("select_050701", "100000"); //司机
     * otherRequestParams.put("select_050702", "100000"); //乘客
     * otherRequestParams.put("select_050210", "20000"); //划痕
     * otherRequestParams.put("select_050310", "329800.0"); //其他
     * 如上面的代码描述的,三者是double类型的 值为2000000.0 那么在请求中必须为2000000. 司机,乘客,划痕同理, 其他险种则不受此规则限制.
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
     * 处理套餐中boolean类型险种对应的请求, 如各种不计免赔, 选中结果为1,不选为-1
     *
     * @param ip       套餐对象
     * @param propName InsurancePackage中的属性名
     * @return 该险种的在报价请求参数中的值
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String amountConvertFromIPBoolean(InsurancePackageVO ip, String propName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        return (Boolean) executeReadMethod(propName, ip) ? "1" : "-1";
    }

    /**
     * 从套餐中获取玻璃险的选项, glass 0-不投保 1-国产 2-进口, 对应人保的0-不投保, 10-国产, 20-进口
     *
     * @param ip
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String glassConvertFromIPBoolean(InsurancePackageVO ip) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        String glassValue = executeReadMethod("glass", ip).toString();
        return glassValue.equals("1") ? "10" : glassValue.equals("2") ? "20" : "0";
    }

    /**
     * 对于不支持的险种直接以-1为值,即在保险公司官网不选择
     *
     * @return
     */
    private String unsupportItem() {
        return "-1";
    }

    /**
     * 对于不可选的险种要置为空
     *
     * @return
     */
    private String disabledItem() {
        return "";
    }

    /**
     * 从套餐中获取指定专修厂的选项, -1-不投保, 1-投保
     *
     * @param ip
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String specialFactoryConvertFromIPString(InsurancePackageVO ip) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        String value = executeReadMethod("specialFactory", ip).toString();
        return value.equals("1") ? "1" : "-1";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

