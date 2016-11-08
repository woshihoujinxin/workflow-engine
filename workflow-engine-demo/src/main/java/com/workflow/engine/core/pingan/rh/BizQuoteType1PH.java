package com.workflow.engine.core.pingan.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.workflow.engine.core.pingan.config.Constants._TYPE1_ITEMS_MAPPINGS;

/**
 * 第一类报价请求处理器
 * Created by houjinxin on 16/4/8.
 */
public class BizQuoteType1PH extends AbstractBizQuotePH {

    private static final Logger logger = LoggerFactory.getLogger(BizQuoteType1PH.class);

    @Override
    protected String[][] getItemsMappings() {
        return _TYPE1_ITEMS_MAPPINGS;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
