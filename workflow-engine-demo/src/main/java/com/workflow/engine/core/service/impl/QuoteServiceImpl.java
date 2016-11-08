package com.workflow.engine.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.workflow.engine.core.service.IThirdPartyService;
import com.workflow.engine.core.service.QuoteService;
import com.workflow.engine.core.service.entity.vo.SupplierVO;
import com.workflow.engine.core.service.entity.vo.WangXiaoResultVO;
import com.workflow.engine.core.service.entity.vo.WangXiaoVO;
import com.workflow.engine.core.service.factory.ThirdPartyServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("quoteService")
public class QuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);
    @Autowired
    ThirdPartyServiceFactory thirdPartyServiceFactory;
    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    /**
     * 单家保险公司报价
     *
     * @param vo
     * @param businessID
     */
    private void singleSupplierQuote(WangXiaoVO vo, String businessID) {
        WangXiaoResultVO.Data.Quote quote;
        SupplierVO supplier = vo.getSuppliers().get(0);
        String supplier_name = supplier.getSupplierName();
        String supplier_code = supplier.getSupplierCode();
    }

    @Override
    public String quote(WangXiaoVO vo) {

        List<SupplierVO> suppliers = vo.getSuppliers();
        if (suppliers.size() == 1) {
            IThirdPartyService thirdPartyService = thirdPartyServiceFactory.getServiceBySuppierCode(suppliers.get(0).getSupplierCode());
            Object object = thirdPartyService.quote(vo);
            System.out.println(JSON.toJSONString(object));
        }
        return "成功";
    }

}
