package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JsoupHelper;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 获取所有的人保支持的险种
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class CalculateFee implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CalculateFee.class);

    private static final String _API_CALCULATE_FEE = "http://www.epicc.com.cn/wap/carProposal/car/calculateFee";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_CALCULATE_FEE, requestParams, PiccBizUtil.getHeaders());
        //匹配页面表格中ID不为空的TR,即获取险种所在TR
        List<Map<String, Object>> insuranceList = getEnabledInsuranceItems(response);
        if (insuranceList != null) {
            logger.info("获取套餐选择界面成功,可选险种信息为:\n{}", insuranceList);
            context.put("insuranceList", insuranceList);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("获取套餐失败,响应为: \n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    /**
     * 获取可选的险种信息
     *
     * @param responseHtml 代表HTML的字符串
     * @return 套餐列表
     */
    public List<Map<String, Object>> getEnabledInsuranceItems(String responseHtml) {
        //匹配页面表格中ID不为空的TR,即获取险种所在TR
        Elements trs = JsoupHelper.getContentElements(responseHtml, ".commercial_combo table tr[id]");
        if (trs != null) {
            List<Map<String, Object>> insuranceItems = new ArrayList<Map<String, Object>>();
            for (Element tr: trs) {
                Map<String, Object> insuranceItem = new HashMap<String, Object>();
                String insuranceCode = tr.id().replaceAll("tr_", "");
                Elements tds = tr.select("td");
                String insuranceCNName = tds.eq(0).text();
                List<String> optionList = new ArrayList<String>();
                Elements options = tds.eq(2).select("label #select_"+insuranceCode+" option");
                for (Element option: options) {
                    String value = option.val();
                    optionList.add(value);
                }
                insuranceItem.put("insuranceCode", insuranceCode);
                insuranceItem.put("insuranceCNname", insuranceCNName);
                insuranceItem.put("options", optionList);
                insuranceItems.add(insuranceItem);
            }
            return insuranceItems;
        }
        return null;
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String licenseNo = (String) context.get("licenseNo");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        String enrollDate = (String) context.get("enrollDate");
        String engineNo = (String) context.get("engineNo");
        String vinNo = (String) context.get("frameNo");
        String frameNo = (String) context.get("frameNo");
        String carOwner = (String) context.get("carOwner");
        String insuredName = (String) context.get("carOwner");
        String isRenewal = (String) context.get("isRenewal");
        String identity = (String) context.get("identity");
        String interimNo = (String) context.get("interimNo");

//        String seatCount = "5";
//        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
//        Map<String, String> appliInfo = (Map<String, String>) vehicleInfo.get("appliInfo");
//        Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
//        Map<String, String> insuredInfo = (Map<String, String>) vehicleInfo.get("insuredInfo");
//        String standardName = appliCarInfo.get("standardName"); //上游步骤设置
//        String appliEmail = appliInfo.get("appliEmail");
//        String appliMobile =  appliInfo.get("appliMobile");
//        String insuredEmail = insuredInfo.get("insuredEmail");
//        String insuredMobile = insuredInfo.get("insuredMobile");

        Map<String, String> requestParams = new HashMap<String, String>();
        handleSomeParamsByReuseCarData(context, requestParams);
        requestParams.put("carInfo.lastCityCheck", "");
        requestParams.put("carInfo.ibaoyangFlag", "");
        requestParams.put("carInfo.xuanzeiflag", "");
        requestParams.put("carInfo.refuel", "");
        requestParams.put("carInfo.discountGas_Flag", "");
        requestParams.put("carInfo.invoiceflag", "");
        requestParams.put("carInfo.calcBIFlag", "");
        requestParams.put("carInfo.carDataReuse", "1");
        requestParams.put("carInfo.lastcarownername", carOwner);
        requestParams.put("carInfo.proSelected", proSelected);
        requestParams.put("carInfo.citySelected", citySelected);
        requestParams.put("carInfo.areaCodeLast", "");
        requestParams.put("carInfo.cityCodeLast", "");
        requestParams.put("carInfo.insuredIdentifSex", "");
        requestParams.put("carInfo.insuredBirthday", "");
        requestParams.put("carInfo.kindName_050291", "");
        requestParams.put("carInfo.kindName_050924", "");
        requestParams.put("carInfo.kindName_050252", "");
        requestParams.put("carInfo.amountList_050270", "");
        requestParams.put("carInfo.amount_050270", "");
        requestParams.put("carInfo.premium_050270", "");
        requestParams.put("carInfo.amountList_050935", "");
        requestParams.put("carInfo.amount_050935", "");
        requestParams.put("carInfo.premium_050935", "");
        requestParams.put("carInfo.amountList_050641", "");
        requestParams.put("carInfo.amount_050641", "");
        requestParams.put("carInfo.premium_050641", "");
        requestParams.put("carInfo.amountList_050642", "");
        requestParams.put("carInfo.amount_050642", "");
        requestParams.put("carInfo.premium_050642", "");
        requestParams.put("carInfo.amountList_050451", "");
        requestParams.put("carInfo.amount_050451", "");
        requestParams.put("carInfo.premium_050451", "");
        requestParams.put("carInfo.amountList_050919", "");
        requestParams.put("carInfo.amount_050919", "");
        requestParams.put("carInfo.premium_050919", "");
        requestParams.put("carInfo.amountList_050918", "");
        requestParams.put("carInfo.amount_050918", "");
        requestParams.put("carInfo.premium_050918", "");
        requestParams.put("carInfo.amountList_050917", "");
        requestParams.put("carInfo.amount_050917", "");
        requestParams.put("carInfo.premium_050917", "");
        requestParams.put("carInfo.amountList_050643", "");
        requestParams.put("carInfo.amount_050643", "");
        requestParams.put("carInfo.premium_050643", "");
        requestParams.put("carInfo.machineID", "");
        requestParams.put("carInfo.carrier", "");
        requestParams.put("carInfo.cooperId", "");
        requestParams.put("carInfo.cooperType", "");
        requestParams.put("carInfo.cooperNote", "");
        requestParams.put("carInfo.callUrl", "");
        requestParams.put("carInfo.cooperTelephoneNo", "");
        requestParams.put("carInfo.cooperEmail", "");
        requestParams.put("carInfo.cooperIdNum", "");
        requestParams.put("callBack.callUrl", "");
        requestParams.put("callBack.cooperId", "");
        requestParams.put("callBack.note", "");
        requestParams.put("callBack.riskCode", "");
        requestParams.put("callBack.sumPrice", "");
        requestParams.put("callBack.payUrl", "");
        requestParams.put("cooperE", "");
        requestParams.put("driverInfoList", "");
        requestParams.put("head.requestType", "sds");
        requestParams.put("head.requestCode", "20132004");
        requestParams.put("head.uuid", "1234");
        requestParams.put("carInfo.progressBar", "1");
        requestParams.put("head.sessionId", sessionId);
        requestParams.put("head.channelNo", "2");
        requestParams.put("carCodex", "");
        requestParams.put("carInfo.isRenewal", isRenewal);
        requestParams.put("carInfo.isNewCar", "1");
        requestParams.put("carInfo.licenseNo", licenseNo);
        requestParams.put("carInfo.licenseFlag", "1");
        requestParams.put("carInfo.beforeProposalNo", "");
        requestParams.put("carInfo.carOwner", carOwner);
        requestParams.put("carInfo.vinNo", vinNo);
//        requestParams.put("carInfo.appliMobile", appliMobile);
//        requestParams.put("carInfo.appliEmail", appliEmail);
        requestParams.put("carInfo.guohuselect", "");
        requestParams.put("carInfo.haveLoan", "2");
        requestParams.put("carInfo.fullAmountName", "");
        requestParams.put("carInfo.runAreaCodeName", "");
//        requestParams.put("carInfo.seatCount", seatCount);
        requestParams.put("carInfo.weiFaName", "");
        requestParams.put("carInfo.assignDriver", "2");
        requestParams.put("carInfo.assignDriverJson", "");
        requestParams.put("carInfo.endDateSY", endDate);
        requestParams.put("carInfo.endHourSY", "24");
        requestParams.put("carInfo.startDateCI", "");
        requestParams.put("carInfo.starthourCI", "");
        requestParams.put("carInfo.endDateCI", "");
        requestParams.put("carInfo.endhourCI", "");
        requestParams.put("carInfo.certificatedate", "");
        requestParams.put("carInfo.isNeedQueryCarModel", "1");
        requestParams.put("carInfo.aliasNameForIn", "");
        requestParams.put("carInfo.nonlocalflag", "0");
        requestParams.put("carInfo.hasBz", "");
        requestParams.put("carInfo.insuredIdentifyType", "");
        requestParams.put("carInfo.insuredIdentifyAddr", "");
        requestParams.put("carInfo.insuredName", insuredName);
        requestParams.put("carInfo.insuredIdentifyNumber", "");
        requestParams.put("carInfo.argueSolution", "");
        requestParams.put("carInfo.insuredAndOwnerrelate", "");
        requestParams.put("carInfo.invoiceTitle", "");
        requestParams.put("carInfo.appliPhoneNumber", "");
        requestParams.put("carInfo.appliAddName", "");
        requestParams.put("carInfo.appliAddress", "");
        requestParams.put("carInfo.appliName", insuredName);
//        requestParams.put("carInfo.insuredMobile", insuredMobile);
//        requestParams.put("carInfo.insuredEmail", insuredEmail);
        requestParams.put("carInfo.appliIdentifyNumber", "");
        requestParams.put("carInfo.appliIdentifyType", "");
        requestParams.put("carInfo.carIdentifyType", "");
        requestParams.put("carInfo.carIdentifyNumber", "");
        requestParams.put("carInfo.deliverInfoAddress", "");
        requestParams.put("carInfo.deliverInfoDistrict", "");
        requestParams.put("carInfo.taxPayerName", "");
        requestParams.put("carInfo.taxPayerIdentNo", "");
        requestParams.put("carInfo.taxpayertype", "");
        requestParams.put("carInfo.jqDataFlag", "1");
        requestParams.put("carInfo.retrieveFlag", "");
        requestParams.put("carInfo.bzEnable", "1");
        requestParams.put("isFocus", "");
        requestParams.put("carInfo.bzrealtime", "");
        requestParams.put("carInfo.blanclistflag", "true");
        requestParams.put("carInfo.guohuflag", "");
        requestParams.put("carInfo.flagForFeeAjax", "1");
        requestParams.put("carInfo.ccaEntryLinks", "");
        requestParams.put("carInfo.ccaEntryId", "");
        requestParams.put("serverDateTime", "");
        requestParams.put("carInfo.ccaId", "");
        requestParams.put("carInfo.ccaFlag", "");
        requestParams.put("carInfo.familyName", "");
        requestParams.put("carInfo.parentId", "");
        requestParams.put("carInfo.queryCityCode", "");
        requestParams.put("carInfo.postCode", "");
        requestParams.put("carInfo.deliverInfoPro", "");
        requestParams.put("carInfo.deliverInfoCity", "");
        requestParams.put("carInfo.xubaocomcode", "");
        requestParams.put("carInfo.majorFactoryAddress", "");
        requestParams.put("carInfo.majorFactoryName", "");
        requestParams.put("carInfo.arbitboardname", "");
        requestParams.put("carInfo.isModify_t", "0");
        requestParams.put("carInfo.isModify_b", "");
        requestParams.put("carInfo.isModify_s", "");
        requestParams.put("carInfo.carOwerIdentifyType", "01");
        requestParams.put("carInfo.carOwerIdentifyNo", identity);
        requestParams.put("carInfo.bjfuel_type", "");
        requestParams.put("carInfo.certificate_type", "");
        requestParams.put("carInfo.certificate_no", "");
        requestParams.put("carInfo.certificate_date", "");
        requestParams.put("carInfo.seatFlag", "");
        requestParams.put("carInfo.assignDriverFlag", "");
        requestParams.put("carInfo.runAreaCodeFlag", "");
        requestParams.put("carInfo.isOutRenewal", "0");
        requestParams.put("carInfo.lastHas050200", "0");
        requestParams.put("carInfo.lastHas050500", "0");
        requestParams.put("carInfo.lastHas050210", "0");
        requestParams.put("carInfo.carKindCI", "");
        requestParams.put("carInfo.traveltaxAddress", "");
        requestParams.put("carInfo.tAX_FLAG_SH", "");
        requestParams.put("carInfo.tAX_FLAG_SH_flag", "");
        requestParams.put("carInfo.buyCarDate_flag", "");
        requestParams.put("carInfo.oldRNew", "");
        requestParams.put("carInfo.carIdentifyAddressSXId_flag", "0");
        requestParams.put("carInfo.carKindSXId_flag", "0");
        requestParams.put("carInfo.carNameSXId_flag", "0");
        requestParams.put("carInfo.lastdamageBI", "");
        requestParams.put("carInfo.noDamyearsBI", "");
        requestParams.put("carInfo.carKindSXId", "");
        requestParams.put("carInfo.carNameSXId", "");
        requestParams.put("carInfo.carIdentifyAddressSXId", "");
        requestParams.put("carInfo.carOwner_remindFlag", "");
        requestParams.put("carInfo.carOwner_remind", "");
        requestParams.put("carInfo.mssFlag", "");
        requestParams.put("carInfo.interimNo", interimNo);
        requestParams.put("resultCode", "");
        requestParams.put("carInfo.countryCode", "");
        requestParams.put("carInfo.countryName", "");
        requestParams.put("carInfo.appliIsResident", "");
        requestParams.put("carInfo.insuredAccidentFlag", "");
        requestParams.put("carInfo.EADJson", "");
        requestParams.put("carInfo.jigeInsured", "");
        requestParams.put("carInfo.jifen", "");
        requestParams.put("carInfo.eadsumPremium", "");
        requestParams.put("carInfo.comName", "");
        requestParams.put("carInfo.startPage", "");
        requestParams.put("carInfo.channel", "");
        requestParams.put("carInfo.isEpolicy", "");
        requestParams.put("carInfo.policyEmail", "");
        requestParams.put("carInfo.beijingSelect", "0");
        requestParams.put("carInfo.frameNo",frameNo);
        requestParams.put("carInfo.engineNo", engineNo);
        requestParams.put("carInfo.enrollDate", enrollDate);
//        requestParams.put("carInfo.vehicle_modelsh", standardName);
        requestParams.put("carInfo.aliasName", "");
        requestParams.put("carInfo.startDateSY", startDate);
        requestParams.put("carInfo.startHourSY", "0");
        requestParams.put("carInfo.loanName", "");
        return requestParams;
    }

    /**
     * 设置部分需要根据reuseCarData的值来确定的请求参数
     * @param context
     * @param requestParams
     */
    private void handleSomeParamsByReuseCarData(Map<String, Object> context, Map<String, String> requestParams){
        String standardName;
        String seatCount;
        String appliEmail;
        String appliMobile;
        String insuredEmail;
        String insuredMobile;
        Map<String, Object> vehicleInfo = (Map<String, Object>) context.get("vehicleInfo");
        if ((boolean) context.get("reuseCarData")) {
            Map<String, String> appliInfo = (Map<String, String>) vehicleInfo.get("appliInfo");
            Map<String, String> appliCarInfo = (Map<String, String>) vehicleInfo.get("appliCarInfo");
            Map<String, String> insuredInfo = (Map<String, String>) vehicleInfo.get("insuredInfo");
            standardName = appliCarInfo.get("standardName"); //上游步骤设置
            seatCount = appliCarInfo.get("seat");
            appliEmail = appliInfo.get("appliEmail");
            appliMobile =  appliInfo.get("appliMobile");
            insuredEmail = insuredInfo.get("insuredEmail");
            insuredMobile = insuredInfo.get("insuredMobile");
        } else {
            standardName = (String) vehicleInfo.get("vehicleFgwCode");
            seatCount = (String) vehicleInfo.get("seat");
            appliEmail = "";
            appliMobile = "";
            insuredEmail = "";
            insuredMobile = "";
        }
        requestParams.put("carInfo.vehicle_modelsh", standardName);
        requestParams.put("carInfo.seatCount", seatCount);
        requestParams.put("carInfo.insuredEmail",insuredEmail);
        requestParams.put("carInfo.insuredMobile", insuredMobile);
        requestParams.put("appliEmail", appliEmail);
        requestParams.put("appliMobile", appliMobile);
    }
}

