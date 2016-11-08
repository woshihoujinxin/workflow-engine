package com.workflow.engine.core.common.annotation;

import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.strategy.ParamGeneratorConfig;
import com.workflow.engine.core.common.strategy.ParamGeneratorScheme;
import com.workflow.engine.core.common.strategy.StrategyType;
import com.workflow.engine.core.common.strategy.ParamGeneratorUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于解析step实现类的注解
 * Created by houjinxin on 16/6/28.
 */
public class AnnotationParser {

    public ParamGeneratorConfig parserAnnotation(Class<? extends IStep> stepClass) {
        ArrayList<ParamGeneratorScheme> schemes = new ArrayList<>();
        Map<String, Integer> paramCountMap = new HashMap<>();
        // 仅获取自身的注解
        Annotation[] annotations = stepClass.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().contains("PGConfig")) {
                PGConfig pgc = (PGConfig) annotation;
                for (PGScheme pgs : pgc.value()) {
                    checkPGScheme(pgs, paramCountMap);
                    String requestParamName = pgs.requestParamName();
                    String valueSourceExpression = pgs.valueSourceExpression();
                    ParamGeneratorScheme scheme;
                    if (valueSourceExpression.equals("")) {
                        scheme = ParamGeneratorUtil.schemeParam(requestParamName)
                                .chooseStrategy(pgs.strategy())
                                .configStrategyNeedParams(pgs.strategyNeedParams());
                    } else {
                        scheme = ParamGeneratorUtil.schemeParam(requestParamName)
                                .valueSourceExpression(pgs.valueSourceExpression())
                                .chooseStrategyIfTrue(pgs.strategyIfTrue())
                                .configStrategyNeedParamsIfTrue(Arrays.equals(pgs.strategyNeedParamsIfTrue(), new String[]{}) ? null : pgs.strategyNeedParamsIfTrue())
                                .chooseStrategyIfFalse(pgs.strategyIfFalse())
                                .configStrategyNeedParamsIfFalse(Arrays.equals(pgs.strategyNeedParamsIfFalse(), new String[]{}) ? null : pgs.strategyNeedParamsIfFalse());
                    }

                    String individualValueSourceExpression = pgs.individualValueSourceExpression();
                    if (!individualValueSourceExpression.equals("")) {
                        scheme.individualValueSourceExpression(individualValueSourceExpression)
                                .chooseIndividualStrategy(pgs.individualStrategy())
                                .configIndividualStrategyNeedParams(pgs.individualStrategyNeedParams());
                    }
                    schemes.add(scheme);
                }
            } else {
                continue;
            }
        }
        ParamGeneratorScheme[] schemesArray = schemes.toArray(new ParamGeneratorScheme[schemes.size()]);
        return ParamGeneratorUtil.configParamScheme(schemesArray);
    }

    private void checkPGScheme(PGScheme pgs, Map<String, Integer> paramCountMap) {
        String requestParamName = pgs.requestParamName();
        if (paramCountMap.get(requestParamName) != null) {
            throw new RuntimeException("注解参数" + requestParamName + "重复配置");
        }
        StrategyType strategyType = pgs.strategy();
        String[] strategyNeedParams = pgs.strategyNeedParams();
        String valueSourceExpression = pgs.valueSourceExpression();
        StrategyType strategyTypeIfTrue = pgs.strategyIfTrue();
        String[] strategyNeedParamsIfTrue = pgs.strategyNeedParamsIfTrue();
        StrategyType strategyTypeIfFalse = pgs.strategyIfFalse();
        String[] strategyNeedParamsIfFalse = pgs.strategyNeedParamsIfFalse();
        String individualValueSourceExpression = pgs.individualValueSourceExpression();
        StrategyType individualStrategy = pgs.individualStrategy();
        String[] individualStrategyNeedParams = pgs.individualStrategyNeedParams();

        if (valueSourceExpression.equals("")) {
            if (strategyTypeIfTrue != StrategyType.NONE || strategyTypeIfFalse != StrategyType.NONE || !Arrays.equals(strategyNeedParamsIfTrue, new String[]{}) || !Arrays.equals(strategyNeedParamsIfFalse, new String[]{})) {
                throw new RuntimeException("参数" + requestParamName + "的值源表达式为空,注解中不可设置strategyIfTrue, strategyIfFalse, strategyNeedParamsIfTrue, strategyNeedParamsIfFalse等属性");
            }
            if (strategyType == StrategyType.NONE || Arrays.equals(strategyNeedParams, new String[]{})) {
                throw new RuntimeException("参数" + requestParamName + "的值源表达式为空,注解中必须设置strategy, strategyNeedParams等属性");
            }
        } else {
            if (strategyTypeIfTrue == StrategyType.NONE || strategyTypeIfFalse == StrategyType.NONE || Arrays.equals(strategyNeedParamsIfTrue, new String[]{}) || Arrays.equals(strategyNeedParamsIfFalse, new String[]{})) {
                throw new RuntimeException("参数" + requestParamName + "的值源表达式不为空,注解中必须设置strategyTypeIfTrue, strategyTypeIfFalse, strategyNeedParamsIfTrue, strategyNeedParamsIfFalse等属性");
            }
            if (strategyType != StrategyType.NONE || !Arrays.equals(strategyNeedParams, new String[]{})) {
                throw new RuntimeException("参数" + requestParamName + "的值源表达式不为空,注解中不可设置strategy, strategyNeedParams等属性");
            }
        }

        if (individualValueSourceExpression.equals("")) {
            if (individualStrategy != StrategyType.NONE || !Arrays.equals(individualStrategyNeedParams, new String[]{})) {
                throw new RuntimeException("参数" + requestParamName + "的个性化值源表达式为空,注解中不可设置individualStrategy, individualStrategyNeedParams等属性");
            }
        } else {
            if (individualStrategy == StrategyType.NONE || Arrays.equals(individualStrategyNeedParams, new String[]{})) {
                throw new RuntimeException("参数" + requestParamName + "的个性化值源表达式为空,注解中必须设置individualStrategy, individualStrategyNeedParams等属性");
            }
        }
        paramCountMap.put(requestParamName, 1);

    }
}
