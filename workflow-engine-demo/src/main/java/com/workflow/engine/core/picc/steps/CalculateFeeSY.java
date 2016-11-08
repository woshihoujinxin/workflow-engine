package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.common.utils.CollectionUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;
import static com.workflow.engine.core.common.utils.CollectionUtil.collect;
import static com.workflow.engine.core.picc.config.Constants._KIND_CODE_MAPPINGS;

/**
 * 商业险报价
 * Created by houjinxin on 16/3/9.
 */
public class CalculateFeeSY implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CalculateFeeSY.class);

    private static final String _API_CALCULATE_FEE_SY = "http://www.epicc.com.cn/wap/carProposal/calculateFee/sy";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map headers = PiccBizUtil.getHeaders();
        headers.put("Origin", "http://www.epicc.com.cn");
        headers.put("Referer", "http://www.epicc.com.cn/wap/carProposal/car/calculateFee");
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_CALCULATE_FEE_SY, requestParams, headers);
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if ("1".equals(resultCode)) {
            getItemsPremium(jsonNode, context);
            //TODO:商业险起保日期获取的逻辑也要经过测试才能验证
            context.put("bizBeginDate", context.get("startDate"));
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("商业险报价成功,响应为: \n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    /**
     * 获取各险种报价保额
     *
     * @param jsonNode 响应JSON
     */
    public void getItemsPremium(JsonNode jsonNode, Map<String, Object> context) throws IOException {
        List<Map<String, String>> items = JacksonUtil.getListNodeByKey(jsonNode, "items");
        List<Map<String, String>> kindCode2Premiums = CollectionUtil.collect(items, new String[]{"kindCode", "premium"});

        Map<String, String> kindCode2Premium = CollectionUtil.collect(kindCode2Premiums, "kindCode", "premium");
        List<Map<String, String>> basicPackage = JacksonUtil.getListNodeByKey(jsonNode, "basicPackage");
        Map<String, String> firstMap = basicPackage.get(0);
        kindCode2Premium.put("total", firstMap.get("premium")); //各险种报价总和
        logger.info("报价成功,报价结果为:\n{}", kindCode2Premium);
        context.put("kindCode2Premium", kindCode2Premium);
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) throws IllegalAccessException, IntrospectionException, InvocationTargetException, ParseException {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String licenseNo = (String) context.get("licenseNo");
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        String enrollDate = (String) context.get("enrollDate");
        String insuredName = (String) context.get("carOwner");
        String engineNo = (String) context.get("engineNo");
        String isRenewal = (String) context.get("isRenewal"); // 0 转保, 1 续保, 2 新车未上牌
        String identifyNumber = (String) context.get("identity");
        String identifyType = "01";
        String birthday = BusinessUtil.getBirthdayFromIdentity(identifyNumber, "yyyy/MM/dd");
        String sex = BusinessUtil.getGenderFromIdentity(identifyNumber, "1", "2");
        String guohuselect = "0"; //是否是过户车,默认不是

        String seatCount;
        String insuredEmail;
        String insuredMobile;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        if ((boolean) context.get("reuseCarData")) {
            Map<String, String> appliInfo = (Map<String, String>) vehicleInfo.get("appliInfo");
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            Map<String, String> insuredInfo = (Map<String, String>) vehicleInfo.get("insuredInfo");
            seatCount = appliCarInfo.get("seat");
            insuredEmail = insuredInfo.get("insuredEmail");
            insuredMobile = insuredInfo.get("insuredMobile");
        } else {
            seatCount = (String) vehicleInfo.get("seat");
            insuredEmail = "";
            insuredMobile = "";
        }

        Map<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("sessionId", sessionId);
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("areaCodeLast", proSelected);
        requestParams.put("cityCodeLast", citySelected);
        requestParams.put("mobile", insuredMobile);
        requestParams.put("email", insuredEmail);
        requestParams.put("identifytype", identifyType);
        requestParams.put("identifynumber", identifyType);
        requestParams.put("birthday", birthday);
        requestParams.put("sex", sex);
        requestParams.put("startdate", startDate);
        requestParams.put("starthour", "0");
        requestParams.put("enddate", endDate);
        requestParams.put("endhour", "24");
        requestParams.put("isRenewal", isRenewal);
        requestParams.put("licenseno", licenseNo);
        requestParams.put("nonlocalflag", "0");
        requestParams.put("licenseflag", "1");
        requestParams.put("engineno", engineNo);
        requestParams.put("vinno", vinNo);
        requestParams.put("frameno", frameNo);
        requestParams.put("newcarflag", "0");
        requestParams.put("isOutRenewal", "0");
        requestParams.put("lastHas050200", "0");
        requestParams.put("lastHas050210", "0");
        requestParams.put("lastHas050500", "0");
        requestParams.put("seatCount", seatCount);
        requestParams.put("seatflag", "");
        requestParams.put("beforeProposalNo", "");
        requestParams.put("enrolldate", enrollDate);
        requestParams.put("transfervehicleflag", "");
        requestParams.put("insuredname", insuredName);
        requestParams.put("fullAmountName", "");
        requestParams.put("guohuselect", guohuselect);
        requestParams.put("runAreaCodeName", "");
        requestParams.put("assignDriver", "2");
        requestParams.put("haveLoan", "2");
        requestParams.put("LoanName", "");
        requestParams.put("weiFaName", "");
        requestParams.put("carDrivers", "");
        requestParams.put("transferdate", "");
        requestParams.put("travelMilesvalue", "");
        requestParams.put("lastdamageBI", "");
        requestParams.put("noDamyearsBI", "");
        requestParams.put("ccaFlag", "");
        requestParams.put("ccaID", "");
        requestParams.put("ccaEntryId", "");
        requestParams.put("BZ_selected", "2");
        //转换套餐到请求参数
        convertInsurancePackage(context, requestParams);
        return requestParams;
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
        if(disabledInsuranceList != null) {
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
        double amount = (double) BusinessUtil.executeReadMethod(propName, ip);
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
        return (Boolean) BusinessUtil.executeReadMethod(propName, ip) ? "1" : "-1";
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
        String glassValue = BusinessUtil.executeReadMethod("glass", ip).toString();
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
     * @param ip
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String specialFactoryConvertFromIPString(InsurancePackageVO ip) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        String value = BusinessUtil.executeReadMethod("specialFactory", ip).toString();
        return value.equals("1") ? "1" : "-1";
    }

}

