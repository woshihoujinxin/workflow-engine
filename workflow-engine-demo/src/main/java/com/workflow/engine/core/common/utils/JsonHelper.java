package com.workflow.engine.core.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JsonHelper {

	
	public static ObjectMapper mapper;
	static {
		mapper =new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES , true);
	}
	
	/**
	 * 转换成标准字符串
	 * @param str
	 * @return
	 */
	public static String convert(String str){
		if(!StringUtils.isBlank(str)){
			return str.replaceAll("\\\\", "").replaceAll("\"[{]", "{").replaceAll("[}]\"", "}").replaceAll("\"\\[", "\\[").replaceAll("\\]\"", "\\]");
		}
		return str;
	}
	
	/**
	 * 查找json　值
	 * @param key
	 * @param jsondata
	 * @return
	 */
	
	public static String findByKey(String key,String jsondata){
		try {
			String temp=formatJson(jsondata);
			JsonNode json=mapper.readTree(temp);
			if(null!=json.findValue(key)){
				return json.findValue(key).asText();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String findSubJsonByKey(String key,String jsonData){
		try {
			String temp=formatJson(jsonData);
			JsonNode json=mapper.readTree(temp);
			return json.findValue(key).toString();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	
	public static <T> T findByKey(String key,String jsondata ,Class<T> c){
		try {
			String temp=formatJson(jsondata);
			JsonNode json=mapper.readTree(temp);
			return (T) JSON.parseObject(json.findValue(key).toString(), c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formatJson(String jsondata){
		return jsondata.replaceAll("\"\"乐驾人生\"", "\"");
	}
	/**
	 * 传入多个key值 返回map
	 * @param keys
	 * @param jsondata
	 * @return
	 */
	
	public static void findByKeys(Map<String,String> keys,String jsondata){
		try {
			JsonNode json=mapper.readTree(jsondata);
			Set<Entry<String, String>> entries = keys.entrySet();
			for (Entry<String, String> entry : entries) {
				if(null!=json.findValue(entry.getKey())){
					keys.put(entry.getKey(), json.findValue(entry.getKey()).asText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @param xpath
	 * @param jsonData
	 */
	public static String findByXpath(String xpath,String jsonData){
		try {
			JsonNode json=mapper.readTree(jsonData);
			if(StringUtils.indexOf(xpath, "/")>0){
				String[] str= StringUtils.split(xpath, "/");
				for (int i = 0; i < str.length-1; i++) {
					json.findParent(str[i]);
				}
				return json.findPath(str[str.length-1]).asText();
			}
			return null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	
	
}
