package com.workflow.engine.core.picc.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 城市cityCode与ProvinceCode的映射
 * Created by houjinxin on 16/3/30.
 */
public class CityCodeMappings {

    public static final Map<String, Map<String, String>> _CITY_CODE_MAPPINGS = new HashMap<String,  Map<String, String>>() {{
        put("110100", new HashMap<String, String>(){{ //北京
            put("cityCode", "11000000");
            put("areaCode", "11000000");
        }});
        put("120100", new HashMap<String, String>(){{ //天津
            put("cityCode", "12000000");
            put("areaCode", "12000000");
        }});
        put("440100", new HashMap<String, String>(){{ //广州
            put("cityCode", "44010000");
            put("areaCode", "44000000");
        }});
        put("440300", new HashMap<String, String>(){{ //深圳
            put("cityCode", "44030000");
            put("areaCode", "44030000"); //也许是人保的BUG,正确的areaCode应该是44000000
        }});
        put("500100", new HashMap<String, String>(){{ //重庆
            put("cityCode", "50000000");
            put("areaCode", "50000000");
        }});
        put("510100", new HashMap<String, String>(){{ //成都
            put("cityCode", "51010000");
            put("areaCode", "51000000");
        }});
    }};

}
