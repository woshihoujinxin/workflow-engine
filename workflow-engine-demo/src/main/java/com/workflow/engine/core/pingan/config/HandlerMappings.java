package com.workflow.engine.core.pingan.config;

import com.workflow.engine.core.common.rh.IParamsHandler;
import com.workflow.engine.core.common.rh.IResponseHandler;
import com.workflow.engine.core.pingan.rh.CreateQuoteType1RH;
import com.workflow.engine.core.pingan.rh.CreateQuoteType2RH;
import com.workflow.engine.core.pingan.steps.CreateQuote;
//import com.keeper.www.pingan.rh.BizQuoteType1PH;
//import com.keeper.www.pingan.rh.BizQuoteType2PH;
//import com.keeper.www.pingan.steps.BizQuote;

import com.workflow.engine.core.pingan.steps.n.BizQuote;
import com.workflow.engine.core.pingan.rh.n.BizQuoteType1PH;
import com.workflow.engine.core.pingan.rh.n.BizQuoteType2PH;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于配置步骤与地区和请求参数的映射关系
 * Created by houjinxin on 16/3/11.
 */
public class HandlerMappings {

    public static final Map<String, Map<String, IParamsHandler>> _STEP_CITY_PH_MAPPING = new HashMap<String, Map<String, IParamsHandler>>(){{
        put(BizQuote.class.getName(),  new HashMap<String, IParamsHandler>(){{
            put("110100",new BizQuoteType1PH()); //北京
            put("120100",new BizQuoteType2PH()); //天津
            put("500100",new BizQuoteType2PH()); //重庆
            put("510100",new BizQuoteType2PH()); //成都
            put("440100",new BizQuoteType2PH()); //广州
            put("440300",new BizQuoteType2PH()); //深圳
        }});
    }};

    public static final Map<String, Map<String, IResponseHandler>> _STEP_CITY_RH_MAPPING = new HashMap<String, Map<String, IResponseHandler>>(){{
        put(CreateQuote.class.getName(), new HashMap<String, IResponseHandler>(){{
            put("110100",new CreateQuoteType1RH()); //北京
            put("120100",new CreateQuoteType2RH()); //天津
            put("500100",new CreateQuoteType2RH()); //重庆
            put("510100",new CreateQuoteType2RH()); //成都
            put("440100",new CreateQuoteType2RH()); //广州
            put("440300",new CreateQuoteType2RH()); //深圳
        }});
    }};

}
