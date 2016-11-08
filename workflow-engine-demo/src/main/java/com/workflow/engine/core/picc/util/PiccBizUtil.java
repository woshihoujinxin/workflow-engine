package com.workflow.engine.core.picc.util;

import com.workflow.engine.core.common.utils.HttpHelper;
import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.utils.JsonValidatorUtil;
import com.workflow.engine.core.pingan.utils.PinganBizUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 人保业务工具类
 * Created by houjinxin on 16/3/28.
 */
public class PiccBizUtil {

    private static String getRam(){
        return String.valueOf((int)(Math.random()*255));
    }

    private static String getIp(){
        return getRam()+"."+getRam()+"."+getRam()+"."+getRam();
    }

    public static Map<String, String> getHeaders() {
        return new HashMap<String, String>() {{
            put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
            put("Referer", "http://www.epicc.com.cn/m/");
            put("Host", "www.epicc.com.cn");
            put("Origin", "http://www.epicc.com.cn");
            put("X-Requested-With", "XMLHttpRequest");
            put("Accept-Language", "zh-CN,zh;q=0.8");
            put("Accept-Encoding", "gzip, deflate");
            put("X-FORWARDED-FOR", getIp());
        }};
    }

    public static HttpResponse sendPostRequest(Map<String, String> requestParams, String requestUrl) throws Exception {
        HttpPost post = new HttpPost(requestUrl);
        HttpHelper.setEntity(post, requestParams);
        HttpHelper.setHeaders(post, PinganBizUtil.getHeaders());
        return HttpHelper.exec(HttpHelper.getHttpClient(), post);
    }

    public static Map<String, Object> handleResponse(HttpResponse response) throws IOException {
        boolean isJson = false;
        String responseData;
        Map<String, Object> responseMap = new HashMap<String, Object>();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            //只有当结果是json时才有意义
            HttpEntity entity = response.getEntity();
            responseData = JsonHelper.convert(EntityUtils.toString(entity, "utf-8"));
            if (entity.getContentType().getValue().contains("application/json")) {
                isJson = true;
            } else {
                isJson = new JsonValidatorUtil().validate(responseData);
            }
        } else {
            responseData = null;
            isJson = false;
        }
        responseMap.put("isJson", isJson);
        responseMap.put("response", responseData);
        return responseMap;
    }

}
