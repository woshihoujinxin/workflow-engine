package com.workflow.engine.core.common.utils;

import com.workflow.engine.expression.ExpressionContext;
import com.workflow.engine.expression.ExpressionEvaluator;
import com.workflow.engine.expression.PreparedExpression;
import com.workflow.engine.expression.datameta.BaseDataMeta;
import com.workflow.engine.expression.datameta.Variable;
import com.workflow.engine.core.common.strategy.IParamsGeneratorStrategy;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.ParamGeneratorScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数生成策略相关工具类
 * Created by houjinxin on 16/6/3.
 */
public class ParamsGeneratorStrategyUtil {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ParamsGeneratorStrategyUtil.class);

    /**
     * 用于缓存表达式的判定结果,以减少不要的判定
     */
    private static final Map<String, PreparedExpression> _PREPARED_EXPRESSION_CACHE = new HashMap<>();

    /**
     * 利用策略生成请求参数
     *
     * @param context
     * @param stepNeedParamsConfig 参数配置方案
     * @param currentHandleObject  当前处理请求参数对象,可能是一个IStep,也可能是一个IParamHandler
     * @return
     * @throws Exception
     */
    public static Map<String, String> generatorParamByStrategy(Map<String, Object> context, ParamGeneratorConfig stepNeedParamsConfig, Object currentHandleObject) throws Exception {
        Map<String, String> requestParams = new LinkedHashMap<>();
        logger.info("当前请求参数由【 " + currentHandleObject.getClass().getSimpleName() + " 】构造");
        Map<StrategyType, IParamsGeneratorStrategy> strategyMapping = (Map<StrategyType, IParamsGeneratorStrategy>) context.get("paramsGeneratorStrategy");
        for (ParamGeneratorScheme paramGeneratorScheme : stepNeedParamsConfig.getParamGeneratorSchemeList()) {
            StrategyType strategy; //策略类型
            String[] strategyNeedParams; //策略所需的参数
            //通过下面的分支语句可以确定当前参数的生成策略,以及策略所需参数
            if (paramGeneratorScheme.isNeedChooseValueSource()) {
                boolean expressionResult = calculateExpression(context, paramGeneratorScheme.getValueSourceExpression());
                if (expressionResult) {
                    strategy = paramGeneratorScheme.getStrategyIfTrue();
                    strategyNeedParams = paramGeneratorScheme.getStrategyNeedParamsIfTrue();
                } else {
                    strategy = paramGeneratorScheme.getStrategyIfFalse();
                    strategyNeedParams = paramGeneratorScheme.getStrategyNeedParamsIfFalse();
                }
                logger.info("参数:【{}】判定表达式:【{}】判定结果【{}】取值策略:【{}】策略所需参数:【{}】",
                        paramGeneratorScheme.getRequestParamName(), paramGeneratorScheme.getValueSourceExpression(), expressionResult, strategy, StringUtils.join(strategyNeedParams, ","));
            } else {
                strategy = paramGeneratorScheme.getStrategy();
                strategyNeedParams = paramGeneratorScheme.getStrategyNeedParams();
                logger.info("参数:【{}】取值策略:【{}】策略所需参数:【{}】", paramGeneratorScheme.getRequestParamName(), strategy, StringUtils.join(strategyNeedParams, ","));
            }
            if (strategy == StrategyType.NULL) { //NULL策略表示不需要该参数
                logger.info("当前步骤不需要参数【{}】", paramGeneratorScheme.getRequestParamName());
                continue;
            }
            //检查参数生成方案参数正确性
            checkParamGeneratorScheme(currentHandleObject, paramGeneratorScheme.getRequestParamName(), strategy, strategyNeedParams);
            String value = strategyMapping.get(strategy).generatorParam(context, strategyNeedParams);
            if (paramGeneratorScheme.isNeedIndividualConfig()) { //通过策略生成的值无法满足需求,单独处理
                strategy = paramGeneratorScheme.getIndividualStrategy();
                strategyNeedParams = paramGeneratorScheme.getIndividualStrategyNeedParams();
                value = calculateExpression(context, paramGeneratorScheme.getIndividualValueSourceExpression())
                        ? strategyMapping.get(strategy).generatorParam(context, strategyNeedParams)
                        : value;
            }
            requestParams.put(paramGeneratorScheme.getRequestParamName(), value);
        }
        logger.info("Step或ParamHandler【{}】构造的请求参数如下:\n{}", currentHandleObject.getClass().getSimpleName(), requestParams);
        return requestParams;
    }

    private static void checkParamGeneratorScheme(Object currentHandleObject, String currentParam, StrategyType strategy, String[] strategyNeedParams) {
        if (strategyNeedParams.length != strategy.getStrategyNeedParamsCount()) {
            throw new RuntimeException("Step或ParamHandler【" + currentHandleObject.getClass().getSimpleName() + "】的参数【" + currentParam + "】的strategyNeedParams配置错误,请检查!");
        }
    }

    /**
     * 使用这个方法一定要保证路径中其他key是Map类型的,最后一个key对应的值是String类型的,否则会出现类型转换错误
     *
     * @param context
     * @param valuePath
     * @return
     */
    public static String parseValuePath(Map<String, Object> context, String valuePath) {
        String[] keys = valuePath.split("\\.");
        String returnValue = "";
        Map<String, Object> temp = context;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].replaceAll("#", ".");
            if (i == keys.length - 1) {
                returnValue = (String) temp.get(key);
            } else {
                temp = (Map<String, Object>) temp.get(key);
            }
        }
        return returnValue;
    }

    /**
     * 用上下文中key的值替换正则中占位符,目前只支持上下文中key的value是字符串类型的处理
     *
     * @param context
     * @param params
     * @return
     */
    public static String replaceMethodParams(Map<String, Object> context, String params) {
        Pattern pattern = Pattern.compile("(?<=\\{)[^}]+");
        Matcher matcher = pattern.matcher(params);
        while (matcher.find()) {
            params = params.replace("{" + matcher.group() + "}", (String) context.get(matcher.group()));
        }
        return params;
    }

    /**
     * 才用预编译加缓存的方式,防止相同表达式重复计算
     * @param context
     * @param expression
     * @return
     */
    private static boolean calculateExpression(Map<String, Object> context, String expression) {
        return (boolean) executePreparedExpression(context, expression);
    }

    public static Object executePreparedExpression(Map<String, Object> context, String expression) {
        ExpressionContext ctx = new ExpressionContext();
        //每次重新将上下文中赋值给表达式上下文
        ctx.putAll(context);
        //固化context,防止表达式执行时context为空
        ctx.put("context", new Variable("context", BaseDataMeta.DataType.DATATYPE_OBJECT, context));
        PreparedExpression pe;
        long begin =  System.currentTimeMillis();
        if(_PREPARED_EXPRESSION_CACHE.get(expression) != null){
            pe = _PREPARED_EXPRESSION_CACHE.get(expression);
            logger.info("从缓存中获取计算表达式:{},所用时间{}", expression, System.currentTimeMillis() - begin);
        } else {
            pe = ExpressionEvaluator.preparedCompile(expression, ctx);
            _PREPARED_EXPRESSION_CACHE.put(expression, pe);
            logger.info("第一次计算表达式:{},所用时间{}", expression, System.currentTimeMillis() - begin);
        }
        return pe.execute();
    }
}
