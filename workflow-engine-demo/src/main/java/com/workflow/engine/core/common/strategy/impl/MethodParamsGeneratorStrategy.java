package com.workflow.engine.core.common.strategy.impl;

import com.workflow.engine.core.common.strategy.IParamsGeneratorStrategy;

import java.util.Map;

import static com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil.executePreparedExpression;

/**
 * 某些参数通过方法获取
 * strategyNeedParams格式: {"函数调用表达式"}
 * 例如生日是通过解析身份证获取的,那么该strategyNeedParams写法为{"$getBirthdayFromIdentity(identity, pattern)"}
 * 注意这个表达式的结构以"$"开头至"("的都是方法名, 括号里的是方法变量, 若其中一个参数pattern需要替换成"yyyy-MM-dd"
 * 在表达式中的写法是{"$getBirthdayFromIdentity(identity, \"yyyy-MM-dd\")"},必须用引号包围,表示一个字符串常量。
 * 另外这里的identity的名字一定是上下文中已经存在的entry的key,即其值为context.get("identity");
 * 否则获取identity值的时候将会是空,可能导致异常。
 * 所有的方法在使用之前都要注册,更多更详细的用法可以看有关IKExpression的测试用例以及文档。
 * Created by houjinxin on 16/5/30.
 */
public class MethodParamsGeneratorStrategy implements IParamsGeneratorStrategy {

    /**
     * @param context
     * @param strategyNeedParams 暂定为只含有一个方法调用表达式的数组
     * @return 这里限制表达式返回值一定是String类型, 所以书写表达式时一定要格外注意, 不用弄错返回值类型
     * @throws Exception
     */
    public String generatorParam(Map<String, Object> context, String[] strategyNeedParams) throws Exception {
        String expression = strategyNeedParams[0];
        return (String) executePreparedExpression(context, expression);
    }
}
