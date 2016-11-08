package com.workflow.engine.core.common.utils;

import java.util.Map;

/**
 * 用于方便使用表达式求职的结果而提供的方法
 * Created by houjinxin on 16/6/27.
 */
public class ExpressionMethodUtil {

    /**
     * 用于在表达式中从上下文中根据路径取值的方法,该方法根据表达式返回不同类型的值
     *
     * @param object
     * @param valuePath
     * @return
     */
    public static Object parseValuePath(Object object, String valuePath) {
        Map<String, Object> context = (Map<String, Object>) object;
        String[] keys = valuePath.split("\\.");
        Object returnValue = null;
        Map<String, Object> temp = context;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].replaceAll("#", ".");
            if (i == keys.length - 1) {
                returnValue = temp.get(key);
            } else {
                temp = (Map<String, Object>) temp.get(key);
            }
        }
        return returnValue;
    }
}
