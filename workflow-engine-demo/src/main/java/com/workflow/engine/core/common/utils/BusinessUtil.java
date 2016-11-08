package com.workflow.engine.core.common.utils;

import com.alibaba.fastjson.JSON;
import com.workflow.engine.core.common.MsgEnum;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.service.entity.vo.InsurancePackageVO;
import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO;
import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO.Data.Quote.InsType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.workflow.engine.core.common.Constants._FLOW_CONTINUE_FLAG;
import static com.workflow.engine.core.common.Constants._FLOW_FINISHED_FLAG;
import static com.workflow.engine.core.common.Constants._STEP_FAILURE_FLAG;
import static com.workflow.engine.core.common.Constants._STEP_SUCCESS_FLAG;
import static com.workflow.engine.core.common.Constants._STEP_SUCCESS_WITH_INFO_FLAG;
import static com.workflow.engine.core.common.utils.DateUtils.*;
import static com.workflow.engine.core.common.utils.DateUtils.parse;

/**
 * 公共的业务工具类
 * Created by houjinxin on 16/3/18.
 */
public class BusinessUtil {

    /**
     * 检查交强险是否选中
     *
     * @param insurancePackage
     * @return
     */
    public static boolean checkCompulsoryIsChoosed(InsurancePackageVO insurancePackage) {
        return insurancePackage.isCompulsory() && insurancePackage.isAutoTax();
    }

    /**
     * 用于创建险种的报价详情对象
     *
     * @param amount
     * @param code
     * @param premium
     * @param original
     * @return
     */
    public static InsType.BizInfo.Detail createDetail(String code, String name, String amount, String premium, String original) {
        InsType.BizInfo.Detail detail = new InsType.BizInfo.Detail();
        detail.setCode(code);
        detail.setName(name);
        detail.setAmount(amount);
        detail.setPremium(premium);
        detail.setOriginal(original);
        return detail;
    }

    /**
     * 为报价结果设置保险公司名称
     *
     * @param logger
     * @param quote
     * @param supplier_name
     * @param supplier_code
     * @return 报价结果
     */
    @Deprecated
    public static WangXiaoResultVO.Data.Quote checkQuoteResult(Logger logger, WangXiaoResultVO.Data.Quote quote, String supplier_name, String supplier_code) {
        quote.setSupplierCode(supplier_code);
        quote.setSupplierName(supplier_name);
        logger.info("{}报价结果如下:\n{}", supplier_name, JSON.toJSON(quote));
        return quote;
    }

    /**
     * 创建失败的报价对象
     *
     * @param serviceName 保险公司服务名
     * @param msgEnum     失败信息
     * @return
     */
    public static WangXiaoResultVO.Data.Quote createFailQuote(String serviceName, MsgEnum msgEnum) {
        WangXiaoResultVO.Data.Quote quote = new WangXiaoResultVO.Data.Quote();
        InsType insType = new InsType();
        InsType.BizInfo bizInfo = new InsType.BizInfo();
        InsType.Force force = new InsType.Force();
        insType.setBizInfo(bizInfo);
        insType.setForceInfo(force);
        quote.setInsType(insType);
        quote.setCode(msgEnum.getCode());
        quote.setMsg(msgEnum.getMsg());
        setSupplierInfoForQuote(serviceName, quote);
        return quote;
    }

    /**
     * 创建包含某一步骤返回信息的Quote对象
     *
     * @param serviceName
     * @param msgEnum
     * @param stepInfo
     * @return
     */
    public static WangXiaoResultVO.Data.Quote createQuoteByStepInfo(String serviceName, MsgEnum msgEnum, Object stepInfo) {
        WangXiaoResultVO.Data.Quote quote = new WangXiaoResultVO.Data.Quote();
        quote.setStepInfo(stepInfo);
        quote.setCode(msgEnum.getCode());
        quote.setMsg(msgEnum.getMsg());
        setSupplierInfoForQuote(serviceName, quote);
        return quote;
    }

    /**
     * 设置保险公司信息,不管是成功的或者失败的报价都会用到这个方法.
     *
     * @param serviceName 保险公司服务类名
     * @param quote       报价结果对象
     */
    public static void setSupplierInfoForQuote(String serviceName, WangXiaoResultVO.Data.Quote quote) {
        if (serviceName.contains("Picc")) {
            quote.setSupplierName(com.workflow.engine.core.picc.config.Constants.SUPPLIER_NAME);
            quote.setSupplierCode(com.workflow.engine.core.picc.config.Constants.SUPPLIER_CODE);
        } else if (serviceName.contains("Pingan")) {
            quote.setSupplierName(com.workflow.engine.core.pingan.config.Constants.SUPPLIER_NAME);
            quote.setSupplierCode(com.workflow.engine.core.pingan.config.Constants.SUPPLIER_CODE);
        } else {
        	quote.setSupplierName("其他");
            quote.setSupplierCode("default");
        }
    }

    /**
     * 成功的步骤状态
     *
     * @param step
     * @return
     */
    public static StepState successfulStepState(IStep step) {
        return new StepState(step.getClass().getName(), _STEP_SUCCESS_FLAG, _FLOW_CONTINUE_FLAG);
    }

    /**
     * 带分支的步骤状态
     *
     * @param step
     * @param forkFlag
     * @return
     */
    public static StepState successfulStepState(IStep step, String forkFlag) {
        return new StepState(step.getClass().getName(), _STEP_SUCCESS_FLAG, _FLOW_CONTINUE_FLAG, forkFlag);
    }

    /**
     * 步骤执行成功但是需要返回信息(如车型列表)
     *
     * @param step
     * @param stepInfo
     * @param msgEnum
     * @return
     */
    public static StepState finishFlowWithStepInfo(IStep step, Object stepInfo, MsgEnum msgEnum) {
        return new StepState(step.getClass().getName(), _STEP_SUCCESS_WITH_INFO_FLAG, _FLOW_FINISHED_FLAG, null, stepInfo, msgEnum);
    }

    /**
     * 失败的步骤状态
     *
     * @param step
     * @return
     */
    public static StepState failureStepState(IStep step) {
        return new StepState(step.getClass().getName(), _STEP_FAILURE_FLAG, _FLOW_FINISHED_FLAG);
    }

    /**
     * 带异常信息的失败的步骤状态
     *
     * @param step
     * @return
     */
    public static StepState failureStepState(IStep step, MsgEnum msgEnum) {
        return new StepState(step.getClass().getName(), _STEP_FAILURE_FLAG, _FLOW_FINISHED_FLAG, null, null, msgEnum);
    }

    /**
     * 执行GET方法, 带Header与不带Header的方法
     *
     * @param requestUrlPrefix
     * @param requestParams
     * @return
     * @throws Exception 
     */
    public static String executeGetMethod(String requestUrlPrefix, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        String requestUrl = requestUrlPrefix + (getEndStr(requestUrlPrefix).equals("?") ? "" : "?") + MapUtil.mapJoinIncludeValueNull(requestParams, false, true);
        return HttpHelper.get(requestUrl, headers);
    }

    public static String executeGetMethod(String requestUrlPrefix, Map<String, String> requestParams) throws Exception {
        return executeGetMethod(requestUrlPrefix, requestParams, null);
    }

    /**
     * 执行Post方法, 带Header与不带Header的方法
     *
     * @param requestUrl
     * @param requestParams
     * @return
     * @throws Exception 
     */
    public static String executePostMethod(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        return HttpHelper.postBackString(requestParams, requestUrl, headers);
    }

    public static String executePostMethod(String requestUrl, Map<String, String> requestParams) throws Exception {
        return executePostMethod(requestUrl, requestParams, null);
    }

    /**
     * 需要根据HttpResponse进行判断的情况,使用下面两个方法
     * @param requestUrl
     * @param headers
     * @return
     * @throws Exception
     */
    public static HttpResponse sendGetRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        requestUrl = requestUrl + (getEndStr(requestUrl).equals("?") ? "" : "?") + MapUtil.mapJoinIncludeValueNull(requestParams, false, true);
        HttpGet get = new HttpGet(requestUrl);
        HttpHelper.setHeaders(get, headers);
        return HttpHelper.exec(HttpHelper.getHttpClient(), get);
    }

    public static HttpResponse sendPostRequest(String requestUrl, Map<String, String> requestParams, Map<String, String> headers) throws Exception {
        HttpPost post = new HttpPost(requestUrl);
        HttpHelper.setEntity(post, requestParams);
        HttpHelper.setHeaders(post, headers);
        return HttpHelper.exec(HttpHelper.getHttpClient(), post);
    }

    /**
     * 获取URL最后一个字符
     * @param url
     * @return
     */
    private static String getEndStr(String url) {
        return url.substring(url.length() - 1, url.length());
    }

    /**
     * 通过反射执行对象某一属性的readMethod
     *
     * @param propName 对象的任意一个属性
     * @param bean     javaBean对象
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object executeReadMethod(String propName, Object bean) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        PropertyDescriptor pd = new PropertyDescriptor(propName, bean.getClass());
        Method method = pd.getReadMethod();
        return method.invoke(bean);
    }

    /**
     * 删除结果中未报出价的险种, List是引用传递,方法不需要返回值
     *
     * @param details
     */
    public static void filterNullPremiumInDetail(List<InsType.BizInfo.Detail> details) {
        //删除一个List中的元素的方法有两种
        //1.用迭代器的remove方法删除。
        //2.从后面删除
//        for (int i = details.size() - 1; i >= 0; i--) {
//            Detail detail = details.get(i);
//            if (detail.getPremium() == null || "".equals(detail.getPremium())) {
//                details.remove(detail);
//            }
//        }
        Iterator<InsType.BizInfo.Detail> it = details.iterator();
        while (it.hasNext()) {
            InsType.BizInfo.Detail detail = it.next();
            if (detail.getPremium() == null || "".equals(detail.getPremium())) {
                it.remove();
            }
        }
    }

    /**
     * 获取指定格式的字符串型生日
     *
     * @param identity
     * @return
     */
    public static String getBirthdayFromIdentity(String identity, String pattern) throws ParseException {
        String birthDay;
        if (identity.length() == 15) {
            birthDay = "19" + identity.substring(6, 12);
        } else {
            birthDay = identity.substring(6, 14);
        }
        return format(parse(birthDay, "yyyyMMdd"), pattern);
    }

    /**
     * 获取性别,各个保险公司性别对应的值不一样,故后两个参数分别表示保险公司对应的男女标识
     *
     * @param identity 身份证
     * @param male 男性标识
     * @param female 女性标识
     * @return
     */
    public static String getGenderFromIdentity(String identity, String male, String female) {
        String gender;
        if (identity.length() == 15) {
            gender = Integer.valueOf(identity.substring(14, 15)) % 2 == 0 ? female : male;
        } else {
            gender = Integer.valueOf(identity.substring(16, 17)) % 2 == 0 ? female : male;
        }
        return gender;
    }

    /**
     * @param identity     身份证号码
     * @param birthdayEnum 获取需求生日类型
     * @return 生日
     */
    public static String getIdentityBirthday(String identity, BirthdayEnum birthdayEnum) {
        int identitySize = identity.length();
        String birthday = "";
        if (birthdayEnum != null && birthdayEnum.equals(BirthdayEnum.Year_Month_Day)) {
            if (!StringUtils.isBlank(identity) && identitySize == 15) {
                birthday = identity.substring(6, 12);
                birthday = "19" + birthday;
                return birthday;
            } else {

                birthday = identity.substring(6, 14);
                return birthday;
            }
        } else {
            if (!StringUtils.isBlank(identity) && identitySize == 15) {
                birthday = identity.substring(8, 12);
                return birthday;
            } else {
                birthday = identity.substring(10, 14);
                return birthday;
            }

        }

    }

    /**
     * 获取身份证后六位
     *
     * @param identity
     * @return
     */
    public static String getLastSixNumberFromIdentity(String identity) {
        String lastSixNumber;
        if (identity.length() == 15) {
            lastSixNumber = identity.substring(9, 15);
        } else {
            lastSixNumber = identity.substring(12, 18);
        }
        return lastSixNumber;
    }

    /**
     * 获取日期年月
     * @param date 日期
     * @param separator 分割符, 有的日期用"-",有的日期用"/"
     * @return
     */
    public static String getYearAndMonthFromDate(String date, String separator){
        return date.replaceAll(separator, "").substring(0,6);
    }

    /**
     * 获取发动机后4位
     * @param engineNo
     * @return
     */
    public static String getEngineNoLastFourNumber(String engineNo) {
        return engineNo.substring(engineNo.length() - 4, engineNo.length());
    }

    /**
     * 四舍五入方法,可以指定精度
     *
     * @param original
     * @param radix
     * @return
     */
    public static double round(double original, int radix) {
        double factor = Math.pow(10.0, radix); //这里一定是10.0 否则会按照整形计算
        return Math.round(original * factor) / factor;
    }

    /**
     * 截取字符串中的中文
     *
     * @param content 输入的内容
     * @return 中文
     */
    public static String extractChinese(String content) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < content.length(); i++) {
            String retContent = content.substring(i, i + 1);
            boolean isChina = retContent.matches("[\u4E00-\u9FA5]");
            if (isChina) {
                sBuffer.append(retContent);
            }
        }
        return sBuffer.toString();
    }

    /**
     * 生成随机数
     *
     * @param start
     * @param end
     * @return
     */
    private static int getRandomNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    /**
     * 生成随机电话号码
     *
     * @return
     */
    public static String getTel() {
        String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
        int index = getRandomNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getRandomNum(1, 888) + 10000).substring(1);
        String third = String.valueOf(getRandomNum(1, 9100) + 10000).substring(1);
        return first + second + third;
    }

    /**
     * 判断一个字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
