package com.workflow.engine.core.pingan.rh;

import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.misc.AbstractCommonQuoteHandler;
import com.workflow.engine.core.common.misc.ILogger;
import com.workflow.engine.core.common.rh.IResponseHandler;
import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO.Data.Quote.InsType.BizInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.createDetail;
import static com.workflow.engine.core.common.utils.BusinessUtil.filterNullPremiumInDetail;

/**
 * 第一类处理报价结果的ResponseHandler
 * Created by houjinxin on 16/4/7.
 */
public abstract class AbstractCreateQuoteRH extends AbstractCommonQuoteHandler implements IResponseHandler, ILogger {

    abstract protected String[][] getItemsMappings();

    @Override
    public StepState handleResponse(Map<String, Object> context, IStep step, Object response) {
        return handleResponse(context, step, response, null);
    }

    @Override
    public StepState handleResponse(Map<String, Object> context, IStep step, Object response, Object... others) {
        return handleQuote(context, step, response, others);
    }

    @Override
    protected void doOnBizCanApply(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, BizInfo bizInfo) {
        Map<String, Map> bizPremium = (Map<String, Map>) context.get("bizPremium");
        Map<String, String> amounts = bizPremium.get("amounts");
        Map<String, String> premiums = bizPremium.get("premiums");
        String bizBeginDate = (String) context.get("bizBeginDate");
        bizInfo.setTotal(premiums.get("totalPremium"));
        bizInfo.setStartDate(bizBeginDate);
        List<BizInfo.Detail> details = getBizDetails(amounts, premiums);
        filterNullPremiumInDetail(details);
        bizInfo.setSuccess(true);
        bizInfo.setDetail(details);
        ins_type.setBizInfo(bizInfo);
    }

    @Override
    protected void doOnForceCanApply(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.Force forceInfo) {
        Map<String, String> forcePremium = (Map<String, String>) context.get("forcePremium");
        String forceBeginDate = (String) context.get("forceBeginDate");
        forceInfo.setStartDate(forceBeginDate);
        forceInfo.setPremium(forcePremium.get("totalPremium"));
        forceInfo.setTax(forcePremium.get("taxPremium"));
        forceInfo.setSuccess(true);
        ins_type.setForceInfo(forceInfo);
    }

    /**
     * 用于获取商业险报价明细, 基类中只支持处理rType1的类型,若有其他类型需要子类去单独处理
     *
     * @param amounts
     * @param premiums
     * @return
     */
    protected List<BizInfo.Detail> getBizDetails(Map<String, String> amounts, Map<String, String> premiums) {
        //根据商业险各险种名称和平安商业险保额字段名称以及保费的映射关系
        List<BizInfo.Detail> details = new ArrayList<>();
        for (String[] itemMapping : getItemsMappings()) {
            if (itemMapping[0].equals("rType1")) {
                BizInfo.Detail detail = createDetail(itemMapping[6], itemMapping[5], amounts.get(itemMapping[2]), premiums.get(itemMapping[3]), "");
                getLogger().info("{}的报价详情为[险种代码:{},险种保额:{},险种保费:{}]", itemMapping[5], itemMapping[6], detail.getAmount(), detail.getPremium());
                details.add(detail);
            }
        }
        return details;
    }

}
