package com.workflow.engine.core.common.strategy.impl;

import com.workflow.engine.core.common.strategy.IParamsGeneratorStrategy;

import java.util.Map;

import static com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil.executePreparedExpression;
import static com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil.parseValuePath;
import static com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil.replaceMethodParams;

/**
 *
 * 针对按照上下文中名称取值的处理
 * strategyNeedParams格式 {"上下文中对应key的路径", "上下文中不存在key时的默认值"}
 * 上下文中对应key的路径中存在从上下文中取值的情况,路径中可以用{key}这种形式表示,表示需要在上下文中取值
 * 如:从cityCodeMappings配置中获取城市对应的城市代码时,可以写成
 *      cityCodeMapping.{cityAreaCode}.cityCode,
 *      cityCodeMapping.{cityAreaCode}.areaCode
 * 表示从上下文中取得cityAreaCode后,若当前城市为北京,那么替换后的路径就是
 *      cityCodeMapping.110100.cityCode,
 *      cityCodeMapping.110100.areaCode
 *
 * 注意路径之间用"."符号,区分级别, 若有参数中间带"."的在配置strategyNeedParams时必须配置为"#"
 * 例如:上下文中的vehicleInfo是一个Map, 其中有一个key为"vehice.Id",若要获取其值,在配置strategyNeedParams时可以这样配置
 * {"vehicleInfo.vehicle#Id", ""}
 *
 * 允许第二个参数为一个函数的表达式,用来根据函数来取值, 当参数中含有$以及(说明是一个函数表达式
 * Created by houjinxin on 16/5/30.
 */
public class ContextParamsGeneratorStrategy implements IParamsGeneratorStrategy {
    @Override
    public String generatorParam(Map<String, Object> context, String[] strategyNeedParams) throws Exception {
        String valuePath = replaceMethodParams(context, strategyNeedParams[0]);
        String defaultValue = strategyNeedParams[1];
        String returnValue = parseValuePath(context, valuePath);
        if (returnValue == null){
            //若defaultValue是函数表达式,那么计算函数获得返回值
            if(defaultValue.contains("$") && defaultValue.contains("(")){
                defaultValue = (String) executePreparedExpression(context, defaultValue);
            }
        }
        return returnValue == null ? defaultValue : returnValue;
    }
}
