package com.workflow.engine.core.common.rh;

import java.util.Map;

/**
 * 用于不同地区请求参数差异的处理,提供了生成请求参数的方法
 * Created by houjinxin on 16/3/11.
 */
public interface IParamsHandler {

    Map<String, String> generateParams(Map<String, Object> context) throws Exception;

    Map<String, String> generateParams(Map<String, Object> context, Object... others) throws Exception;

}
