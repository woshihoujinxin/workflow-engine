package com.workflow.engine.core.pingan.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.workflow.engine.core.pingan.config.Constants._TYPE2_ITEMS_MAPPINGS;

/**
 * 第二类处理报价结果的ResponseHandler
 * Created by houjinxin on 16/4/7.
 */
public class CreateQuoteType2RH extends AbstractCreateQuoteRH {

    private static final Logger logger = LoggerFactory.getLogger(CreateQuoteType2RH.class);

    @Override
    protected String[][] getItemsMappings() {
        return _TYPE2_ITEMS_MAPPINGS;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
