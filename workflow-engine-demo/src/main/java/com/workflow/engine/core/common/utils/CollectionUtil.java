package com.workflow.engine.core.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 集合变换工具类
 * Created by houjinxin on 16/4/6.
 */
public class CollectionUtil {


    /**
     * 暂时用于过滤Map中某些Key对应的 Value不符合条件的Entry
     */
    public interface DataFilter {
        /**
         * 符合过滤条件的返回true
         *
         * @param objs 与判断条件相关的一些参数
         * @return
         */
        boolean doFilter(Object... objs);
    }

    /**
     * 从List+Map结构的数据转换为Map的通用方法, 通常用于从List中的一个Map中抽取一个Key的Value做降级(指由List转为Map)后的Map的Key,
     * 另一个Key的Value做降级后的Map的Entry.
     * 用泛型是为了摆脱类型对变换逻辑的影响.
     * 使用该方法时一般是已经知道originalList的泛型信息,并且确定想要的结果Map中的泛型信息.在不知道具体类型信息时,不要滥用
     * @param originalList 原始数据
     * @param exceptKey 期望的Key
     * @param exceptValue 期望的Value
     * @param <K> 目标Entry的key类型
     * @param <V> 目标Entry的Value类型
     * @param <S> 原始Entry的Key类型
     * @param <T> 原始Entry的Value类型
     * @return
     */
    public static <K, V, S, T> Map<K, V> collect(List<Map<S, T>> originalList, S exceptKey, S exceptValue) {
        Map<K, V> newMap = new LinkedHashMap<K, V>();
        for (Map<S, T> originalMap : originalList) {
            K newMapEntryKey = null;
            V newMapEntryValue = null;
            Iterator<Map.Entry<S, T>> it = originalMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<S, T> entry = it.next();
                if (entry.getKey().equals(exceptKey)) {
                    newMapEntryKey = (K) entry.getValue();
                }
                if (entry.getKey().equals(exceptValue)) {
                    newMapEntryValue = (V) entry.getValue();
                }
            }
            newMap.put(newMapEntryKey, newMapEntryValue);
        }
        newMap.remove(null);
        return newMap;
    }

    /**
     * 收集List + Map结构的数据结构中期望的信息,比如:
     * 一个List中的每个Map 都包含三个键值对 key1 : value1 , key2 : value2, key3 : value3, 若期望的结果只需要获得所有map的key1 : value1,
     * 和key2 : value2,那么可以以下列方式调用本方法:
     * String[] exceptItems = new String[]{"key1","key2"};
     * collect(list, exceptItems)
     *
     * @param originalList 原始List
     * @param exceptKeys   期望的Key
     * @return 目标List
     */
    public static <K, V> List<Map<K, V>> collect(List<Map<K, V>> originalList, K[] exceptKeys) {
        return collect(originalList, exceptKeys, null);
    }

    /**
     * 收集符合条件的Map
     *
     * @param originalList
     * @param exceptKeys
     * @param filter       过滤条件
     * @return
     */
    public static <K, V> List<Map<K, V>> collect(List<Map<K, V>> originalList, K[] exceptKeys, DataFilter filter) {
        List<Map<K, V>> newList = new ArrayList<>();
        for (Map map : originalList) {
            Map<K, V> newMap = collect(map, exceptKeys, filter);
            if (newMap.size() != 0) { //只有在Map有内容的时候才添加
                newList.add(newMap);
            }
        }
        return newList;
    }

    /**
     * 收集Map中的有效entry
     *
     * @param originalMap
     * @param exceptKeys  期望收集到的Entry的key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> collect(Map<K, V> originalMap, K[] exceptKeys) {
        return collect(originalMap, exceptKeys, null);
    }
    
    /**
     * 收集Map中的有效Entry, 并可以过滤符合条件的无效Entry
     *
     * @param originalMap
     * @param exceptKeys
     * @param filter
     * @return
     */
    public static <K, V> Map<K, V> collect(Map<K, V> originalMap, K[] exceptKeys, DataFilter filter) {
        Map<K, V> newMap = new LinkedHashMap<K, V>();
        for (int i = 0; i < exceptKeys.length; i++) {
            if (filter != null && filter.doFilter(exceptKeys[i], originalMap.get(exceptKeys[i]))) { //过滤符合条件的key
                // 清空Map,因为此时的Map已经不是期望的Map了,例如:
                // 原始Map为["a":"xxx", "b":"xxx", "c", "xxx"],期望的Map为["a":"xxx", "b":"xxx"],
                // 但这时发现b的value不符合条件,那么b对应的Entry根本不会进入Map, Map可能的结构为["a","xxx"],
                // 所以要清除这个Map的所有Entry, 然后在跳出循环
                newMap.clear();
                break;
            }
            newMap.put(exceptKeys[i], originalMap.get(exceptKeys[i]));
        }
        newMap.remove(null);
        return newMap;
    }

    public static void main(String[] args) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(){{
            Map<String, Object> listMap = new HashMap<String, Object>(){{
                put("list", new ArrayList<String>() {{
                    add("item1");
                    add("item2");
                    add("item3");
                    add("item4");
                }});
                put("string", "key");
            }};
            Map<String, Object> listMap1 = new HashMap<String, Object>(){{
                put("list1", new ArrayList<String>() {{
                    add("item11");
                    add("item12");
                    add("item13");
                    add("item14");
                }});
                put("string1", "key1");
            }};
//            Map<Integer, Object> listMap = new HashMap<Integer, Object>(){{
//                put(1, new ArrayList<Integer>() {{
//                    add(1);
//                    add(2);
//                    add(3);
//                    add(4);
//                }});
//                put(2, "key");
//            }};
//            Map<Integer, Object> listMap1 = new HashMap<Integer, Object>(){{
//                put(3, new ArrayList<Integer>() {{
//                    add(11);
//                    add(12);
//                    add(13);
//                    add(14);
//                }});
//                put(4, "key");
//            }};
//            add(listMap);
//            add(listMap1);
        }};
        Map<String, List> map = collect(list, "string", "list");
        System.out.println(map.get("key"));
    }
}
