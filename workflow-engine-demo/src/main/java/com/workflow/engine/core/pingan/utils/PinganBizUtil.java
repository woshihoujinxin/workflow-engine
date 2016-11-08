package com.workflow.engine.core.pingan.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * 平安业务工具类
 * Created by houjinxin on 16/3/25.
 */
public class PinganBizUtil {

    private static String getRam(){
        return String.valueOf((int)(Math.random()*255));
    }

    private static String getIp(){
        return getRam()+"."+getRam()+"."+getRam()+"."+getRam();
    }

    /**
     * 设置headers
     * @return
     */
    public static Map<String, String> getHeaders() {
        return new HashMap<String, String>() {{
            put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
            put("X-FORWARDED-FOR", getIp());
        }};
    }

}
