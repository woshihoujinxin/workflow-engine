package com.workflow.engine.core.pingan.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 城市cityCode与ProvinceCode的映射
 * Created by houjinxin on 16/3/30.
 */
public class CityCodeMappings {

    public static final Map<String, String> _CITY_CODE_MAPPINGS = new HashMap<String, String>() {{
        put("110100", "110000"); //北京
        put("120100", "120000"); //天津
        put("500100", "500000"); //重庆
        put("510100", "510000"); //成都
        put("440100", "440000"); //广州
        put("440300", "440000"); //深圳
    }};

}
