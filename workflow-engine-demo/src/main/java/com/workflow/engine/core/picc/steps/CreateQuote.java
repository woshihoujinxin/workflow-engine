package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.misc.AbstractCommonQuoteHandler;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.picc.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 构造Quote对象
 * Created by houjinxin on 16/3/9.
 */
public class CreateQuote extends AbstractCommonQuoteHandler implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CreateQuote.class);

    @Override
    public StepState run(Map<String, Object> context) throws IOException {
        return handleQuote(context, this, null);
    }

    @Override
    protected void doOnBizCanApply(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.BizInfo bizInfo) {
        Map<String, String> bizPremium = (Map<String, String>) context.get("kindCode2Premium");
        Map<String, String> bizAmount = (Map<String, String>) context.get("kindCode2Amount");

        String bizBeginDate = (String) context.get("bizBeginDate");
        bizInfo.setTotal(bizPremium.get("total"));
        bizInfo.setStartDate(bizBeginDate);
        //根据商业险各险种名称和商业险保额字段名称以及保费的映射关系
        List<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail> details = new ArrayList<>();
        for (String[] itemMapping : Constants._KIND_CODE_MAPPINGS) {
            if (itemMapping[3].equals("none")) { //目前阶段不支持的险种不予处理
                continue;
            }
            String amount;
            if (itemMapping[1].equals("glass")) {
                //glass 0-不投保 1-国产 2-进口, 对应人保的0-不投保, 10-国产, 20-进口
                Map<String, String> glassMappings = new HashMap<String, String>(){{
                    put("0","0");
                    put("1","10");
                    put("2","20");
                }};
                amount = bizAmount != null ? glassMappings.get(bizAmount.get(itemMapping[1])) : "0";
            } else {
                amount = bizAmount != null ? bizAmount.get(itemMapping[1]) : "";
            }
            String premium = bizPremium !=null ? bizPremium.get(itemMapping[1]) : "";
            WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail detail = BusinessUtil.createDetail(itemMapping[3], itemMapping[2], amount, premium, "");
            logger.info("{}的报价详情为[险种代码:{},险种保额:{},险种保费:{}]", itemMapping[2], detail.getCode(), detail.getAmount(), detail.getPremium());
            details.add(detail);
        }
        BusinessUtil.filterNullPremiumInDetail(details);
        bizInfo.setDetail(details);
        ins_type.setBizInfo(bizInfo);
    }

    @Override
    protected void doOnForceCanApply(Map<String, Object> context, WangXiaoResultVO.Data.Quote.InsType ins_type, WangXiaoResultVO.Data.Quote.InsType.Force forceInfo) {
        String forceBeginDate = (String) context.get("forceBeginDate");
        forceInfo.setStartDate(forceBeginDate);
        forceInfo.setPremium((String) context.get("compulsoryPremium"));
        forceInfo.setTax((String) context.get("autoTaxPremium"));
        ins_type.setForceInfo(forceInfo);
    }
}

