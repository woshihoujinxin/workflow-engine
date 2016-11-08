package com.workflow.engine.core.common.utils;

import ch.qos.logback.classic.LoggerContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpHelper {
    public static final Logger log = LoggerFactory.getLogger(HttpHelper.class);
    public static CloseableHttpClient httpClient;
    public static HttpClientContext context;

    public static LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    //请求参数设置
    static RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(60000)
            .setConnectTimeout(60000)
            .setConnectionRequestTimeout(60000)
            .setStaleConnectionCheckEnabled(true)
//            .setProxy(new HttpHost("localhost", 8888))
            .build();

    static {
        Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory()).build();

        httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setDefaultCookieSpecRegistry(registry).build();
        context = HttpClientContext.create();
    }

    /**
     * 获得HttpClient
     *
     * @return
     */
    public static HttpClient getHttpClient() {
        return httpClient;
    }


    private interface CallBack {
        String doIt(String url, Map<String, String> parameter, Map<String, String> headers) throws Exception;

        String doIt(String url, String parameter, Map<String, String> headers) throws Exception;
    }

    private static Logger getLoggerByUrl(String url) {
        Logger logger = null;
        if (StringUtils.contains(url, "picc")) {
            logger = lc.getLogger("com.workflow.engine.core.picc");
        } else if (StringUtils.contains(url, "pingan")) {
            logger = lc.getLogger("com.workflow.engine.core.pingan");
        }
        return logger;
    }

    public static String execute(String url, Map<String, String> parameter, Map<String, String> headers, HttpHelper.CallBack callback) throws Exception {
        Logger logger = getLoggerByUrl(url);
        String rt;
//        logger.info("request url ---------->{}", url);
//        logger.info("request params ---------->{}", parameter);
        rt = callback.doIt(url, parameter, headers);
//        logger.info("response value ---------->{}", rt);
        return rt;
    }

    public static String execute(String url, Object parameter, Map<String, String> headers, HttpHelper.CallBack callback) throws Exception {
        Logger logger = getLoggerByUrl(url);
        String rt;
        SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty};
        String data = JSON.toJSONString(parameter, features);
//        logger.info("request url ---------->{}", url);
//        logger.info("request params ---------->{}", data);
        rt = callback.doIt(url, data, headers);
//        logger.info("response value ---------->{}", rt);
        return rt;
    }

    /**
     * @param parameter 请求参数
     * @param url
     * @param headers   headers参数
     * @return
     * @throws Exception
     */
    public static String postBackString(Map<String, String> parameter, String url, Map<String, String> headers) throws Exception {
        return execute(url, parameter, headers, new CallBack() {
            @Override
            public String doIt(String url, Map<String, String> parameter, Map<String, String> headers) throws Exception {
                HttpPost httppost = new HttpPost(url);
                setHeaders(httppost, headers);
                //设置提交所需要的参数以及字符编码
                setEntity(httppost, parameter);
                String str = invoke(httpClient, httppost);
                return str;
            }

            @Override
            public String doIt(String url, String parameter, Map<String, String> headers) throws Exception {
                return null;
            }
        });
    }


    /**
     * @param obj
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    public static String postJsonBackString(Object obj, String url, Map<String, String> headers) throws Exception {
        return execute(url, obj, headers, new CallBack() {
            @Override
            public String doIt(String url, Map<String, String> parameter, Map<String, String> headers) throws Exception {
                return null;
            }

            @Override
            public String doIt(String url, String parameter, Map<String, String> headers) throws Exception {
                HttpPost httppost = new HttpPost(url);
                setHeaders(httppost, headers);
                StringEntity myEntity = new StringEntity(parameter, "UTF-8");
                httppost.setEntity(myEntity);
                String str = invoke(httpClient, httppost);
                return str;
            }
        });
    }

    /**
     * @param url
     * @param params  请求参数
     * @param c       返回对象类型
     * @param headers
     * @return
     * @throws Exception
     */
    public static <T> T postJsonBackObject(String url, Object params, Class<T> c, Map<String, String> headers) throws Exception {
        return JSON.parseObject(postJsonBackString(params, url, headers), c);
    }

    /**
     * @param url
     * @param params
     * @param c
     * @param headers
     * @return
     * @throws Exception
     */
    public static <T> T postBackObject(String url, Map<String, String> params, Class<T> c, Map<String, String> headers) throws Exception {
        return JSON.parseObject(postBackString(params, url, headers), c);
    }

    /**
     * 通过传入参数设置header
     *
     * @param request
     * @param headers
     */
    public static void setHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (null != headers) {
            Set<Entry<String, String>> entries = headers.entrySet();
            for (Entry<String, String> entry : entries) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 通过传入参数设置entity
     *
     * @param httpPost
     * @param parameter
     * @throws Exception
     */
    public static void setEntity(HttpPost httpPost, Map<String, String> parameter) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (parameter != null) {
            Set<String> keySet = parameter.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, parameter.get(key)));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
    }

    /**
     * 通过get方法请求url
     *
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    public static String get(String url, Map<String, String> headers) throws Exception {
        return execute(url, null, headers, new CallBack() {
            @Override
            public String doIt(String url, Map<String, String> parameter, Map<String, String> headers) throws Exception {
                HttpGet get = new HttpGet(url);
                setHeaders(get, headers);
                //设置提交所需要的参数以及字符编码
                return invoke(httpClient, get);
            }

            @Override
            public String doIt(String url, String parameter, Map<String, String> headers) throws Exception {
                return null;
            }
        });
    }

    /**
	 * 通过get方法请求url
	 * @param url
	 * @param headers
	 * @return
     * @throws Exception
	 */
	public static String get(String url,Map<String, String> parameter,Map<String,String> headers) throws Exception{

		return execute(url, parameter, headers, new CallBack() {

			@Override
			public String doIt(String url, Map<String, String> parameter, Map<String, String> headers) throws Exception {
				Iterator iter = parameter.entrySet().iterator();
				while (iter.hasNext()) {
					Entry entry = (Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					url = url + "&" + key +"="+val;
				}
				HttpGet get = new HttpGet(url);
				setHeaders(get, headers);
					//设置提交所需要的参数以及字符编码
				String str = invoke(httpClient,get);
				return str;
			}

			@Override
			public String doIt(String url, String parameter, Map<String, String> headers) throws Exception {
				return null;
			}
		});
	}


    /**
     * 兼容post和get方法请求
     *
     * @param httpclient
     * @param httpRequest
     * @return
     */
    public static String invoke(HttpClient httpclient, HttpUriRequest httpRequest) throws  Exception{
        HttpResponse response;
        String responseData;
        response = exec(httpclient, httpRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            responseData = EntityUtils.toString(response.getEntity(), "utf-8");
        } else {
            responseData = "http error code " + statusCode;
        }

        return JsonHelper.convert(responseData);
    }

    /**
     * 执行请求
     * @param httpclient
     * @param httpRequest
     * @return
     */
    public static HttpResponse exec(HttpClient httpclient, HttpUriRequest httpRequest) throws IOException {
        return httpclient.execute(httpRequest, context);
    }

    /**
     * 下载图片
     *
     * @param url
     */
    public static void downImage(String url, String filePath, String fileName) throws IOException {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream input = entity.getContent();
            FileUtils.forceMkdir(new File(filePath));
            OutputStream output = new FileOutputStream(new File(filePath + fileName));
            IOUtils.copy(input, output);
            input.close();
            output.flush();
            output.close();
        }
    }
}
