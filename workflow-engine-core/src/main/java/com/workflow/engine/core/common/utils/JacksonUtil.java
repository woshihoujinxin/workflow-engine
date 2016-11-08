package com.workflow.engine.core.common.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 用Jackson框架提供的机制处理json数据
 * Created by houjinxin on 16/3/23.
 */
public class JacksonUtil {
    private static final ObjectMapper _MAPPER = new ObjectMapper();

    /**
     * 根据key的名称取得值为String类型的节点内容
     *
     * @param rootNode
     * @param key      节点key
     * @return 节点内容
     * @throws IOException
     */
    public static String getStringNodeByKey(JsonNode rootNode, String key) throws IOException {
        return getJsonNodeByKey(rootNode, key, String.class);
    }

    /**
     * 根据key的名称取得值为Integer类型的节点内容
     *
     * @param rootNode
     * @param key      节点key
     * @return 节点内容
     * @throws IOException
     */
    public static Integer getIntegerNodeByKey(JsonNode rootNode, String key) throws IOException {
        return getJsonNodeByKey(rootNode, key, Integer.class);
    }

    /**
     * 根据key的名称取得值为Map结构的节点内容
     *
     * @param rootNode
     * @param key      节点key
     * @return 节点内容
     * @throws IOException
     */
    public static Map<String, String> getMapNodeByKey(JsonNode rootNode, String key) throws IOException {
        return getJsonNodeByKey(rootNode, key, Map.class);
    }

    /**
     * 根据key的名称取得值为List结构的节点内容
     *
     * @param rootNode
     * @param key      节点key
     * @return 节点内容, 该方法虽然声明返回值类型为List<Map<String, String>>,
     * 但是,如果Map的entry的value是数值可能返回Double Integer等类型.这是会出现类型转换异常
     * @throws IOException
     */
    public static List<Map<String, String>> getListNodeByKey(JsonNode rootNode, String key) throws IOException {
        return getJsonNodeByKey(rootNode, key, List.class);
    }

    public static <T> T getJsonNodeByKey(JsonNode rootNode, String key, Class<T> clazz) throws IOException {
        JsonNode targetNode;
        if (key == null || key.equals("")) {
            targetNode = rootNode;
        } else {
            targetNode = rootNode.findValue(key);
        }
        return _MAPPER.readValue(targetNode, clazz);
    }

    public static <T> T getJsonNodeByKeyAndType(JsonNode rootNode, String key, TypeReference typeReference) throws IOException {
        JsonNode targetNode;
        if (key == null || key.equals("")) {
            targetNode = rootNode;
        } else {
            targetNode = rootNode.findValue(key);
        }
        return _MAPPER.readValue(targetNode, typeReference);
    }

    /**
     * 将JSON格式的响应结果转换为JsonNode
     *
     * @param jsonString
     * @return
     * @throws IOException
     */
    public static JsonNode getJsonNode(String jsonString) throws IOException {
        return _MAPPER.readTree(jsonString);
    }

    public static Map getHashMap(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        Map map = _MAPPER.readValue(jsonString, Map.class);
        return map;
    }

    /**
     * 用途：该方法用于Json数据转换为Map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.parseObject(jsonStr);
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if (v instanceof JSONArray) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Iterator<Object> it = ((JSONArray) v).iterator();
                while (it.hasNext()) {
                    JSONObject json2 = (JSONObject) it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }
}
