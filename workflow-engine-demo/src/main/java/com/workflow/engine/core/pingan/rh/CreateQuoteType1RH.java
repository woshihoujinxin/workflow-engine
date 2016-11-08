package com.workflow.engine.core.pingan.rh;

import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO;
import com.workflow.engine.core.common.utils.BusinessUtil;
import com.workflow.engine.core.pingan.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 第一类处理报价结果的ResponseHandler
 * Created by houjinxin on 16/4/7.
 */
public class CreateQuoteType1RH extends AbstractCreateQuoteRH {

    private static final Logger logger = LoggerFactory.getLogger(CreateQuoteType1RH.class);

    @Override
    protected String[][] getItemsMappings() {
        return Constants._TYPE1_ITEMS_MAPPINGS;
    }

    @Override
    protected List<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail> getBizDetails(Map<String, String> amounts, Map<String, String> premiums){
        //执行父类的getDetails处理rType1类型的险种
        List<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail> details = super.getBizDetails(amounts, premiums);
        List<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail> combinedExemptList = populateCombinedExemptList(amounts, premiums);
        details.addAll(combinedExemptList);
        return details;
    }

    /**
     * 计算捆绑到一起的险种的各项值,主要是车上人员司机和乘客, 以及附加险(自燃,涉水,划痕)的不计免赔
     * @param amounts
     * @param premiums
     * @return
     */
    private List<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail> populateCombinedExemptList(Map<String, String> amounts, Map<String, String> premiums) {
        List<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail> details = new ArrayList<WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail>();
        for (Object[] combinedItemMapping : Constants._COMBINED_ITEM_MAPPINGS) {
            String exemptAmountName = (String) combinedItemMapping[0]; //主险不计免赔保额属性名, 用来取保额的值 这里是指套餐投保状态
            String exemptPremiumName = (String) combinedItemMapping[1]; //主险不计免赔保费属性名, 用来取不计免赔保费总额
            String[][] miscItemMappings = (String[][]) combinedItemMapping[2];
            int mappingsLength = miscItemMappings.length; //映射长度
            int times = 0; //循环执行次数
            double temp = 0; //临时变量用于记录已经处理的险种的保费和
            double premium; //各项的保费
            WangXiaoResultVO.Data.Quote.InsType.BizInfo.Detail detail = null;
            for (String[] miscItemMapping : miscItemMappings) {
                String premiumName = miscItemMapping[0]; //主险保费属性名
                String item = miscItemMapping[1]; //套餐主险属性名, TODO: 用于在计算保费时判断险种是否投保
                String exemptItem = miscItemMapping[2]; //套餐主险不计免赔属性名
                String cnName = miscItemMapping[3]; //险种中文名称
                if (mappingsLength - 1 == times) { //最后一项用减法,防止出现差一分钱的问题
                    double sum = Double.valueOf(premiums.get(exemptPremiumName));
                    premium =  sum - temp;
                } else {
                    premium = Double.valueOf(premiums.get(premiumName)) * 0.15;
                    temp += premium;
                }
                detail = BusinessUtil.createDetail(exemptItem, cnName, amounts.get(exemptAmountName), String.valueOf(BusinessUtil.round(premium, 2)), "");
                details.add(detail);
                logger.info("{}的报价详情为[险种代码:{},险种保额:{},险种保费:{}]", cnName, exemptItem, detail.getAmount(), detail.getPremium());
                times++;
            }
        }
        return details;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
