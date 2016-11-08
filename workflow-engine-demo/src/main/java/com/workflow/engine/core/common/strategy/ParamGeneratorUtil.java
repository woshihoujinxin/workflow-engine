package com.workflow.engine.core.common.strategy;

/**
 * 参数生成方案配置用到的表达式构建器
 * Created by houjinxin on 16/6/12.
 */
public class ParamGeneratorUtil {

    public static ParamGeneratorScheme schemeParam(String requestParamName) {
        ParamGeneratorScheme pgm = new ParamGeneratorScheme();
        return pgm.setRequestParamName(requestParamName);
    }

    public static ParamGeneratorConfig configParamScheme(ParamGeneratorScheme... paramGeneratorSchemes) {
        ParamGeneratorConfig config = new ParamGeneratorConfig();
        for (ParamGeneratorScheme paramGeneratorScheme : paramGeneratorSchemes) {
            config.addParamGeneratorMapping(paramGeneratorScheme);
        }
        return config;
    }

    public static void main(String[] args) {
        ParamGeneratorConfig ParamGeneratorConfig = configParamScheme(
                schemeParam("a")
                        .valueSourceExpression("a&b&c")
                        .chooseStrategyIfTrue(StrategyType.METHOD)
                        .configStrategyNeedParamsIfTrue("a", "b", "c")
                        .chooseStrategyIfFalse(StrategyType.FIXED)
                        .configStrategyNeedParamsIfFalse("a", "b", "c", "d"),
                schemeParam("a")
                        .chooseStrategy(StrategyType.CONTEXT)
                        .configStrategyNeedParams("a", "b", "c")
        );

        for (ParamGeneratorScheme paramGeneratorScheme : ParamGeneratorConfig.getParamGeneratorSchemeList()){
            StrategyType strategy;
            String[] strategyNeedParams;
            if(paramGeneratorScheme.isNeedChooseValueSource()){
                String expression = paramGeneratorScheme.getValueSourceExpression();
//                caculateExpression(expression);
                if (false){
                    strategy = paramGeneratorScheme.getStrategy();
                    strategyNeedParams = paramGeneratorScheme.getStrategyNeedParamsIfTrue();
                } else {
                    strategy = paramGeneratorScheme.getStrategy();
                    strategyNeedParams = paramGeneratorScheme.getStrategyNeedParamsIfFalse();
                }
            } else {
                strategy = paramGeneratorScheme.getStrategy();
                strategyNeedParams = paramGeneratorScheme.getStrategyNeedParams();
            }
            runStrategy(strategy, strategyNeedParams);
        }

    }

    private static void runStrategy(StrategyType strategy, String[] strategyNeedParams) {
        System.out.println(strategy);
        for (String params: strategyNeedParams) {
            System.out.println(params);
        }
    }
}
