package com.workflow.engine.core.common.strategy;

import com.workflow.engine.core.common.utils.ParamsGeneratorStrategyUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 请求参数生成方案
 * 生成请求参数的过程是:
 * 1、指定请求参数名
 * 2、确定是否该参数的值要通过字符串逻辑表达式确定取值来源
 * 3、若是, 判断逻辑表达式的真假, 配置相应取值策略以及策略参量
 * 4、若否, 直接设置取值策略以及策略参量
 *
 * 一般情况下,设置了策略以及策略参量,都可以取到正确的值,但有一些情况,仍然不可以取到正确的值的,例如:
 * 1、通过METHOD策略获得性别,返回的值是F和M,但是请求中需要的值却是1和2。
 * 2、通过CONTEXT策略获得车型的车座数时,获取的是0。这时候需要设置一个值,但是它代表的是从上下文中取值为null时要指定的默认值。显然不能满足需求。
 * 这时候就需要一些个性化的配置了。
 *
 * 具体实现参见:
 * {@link ParamsGeneratorStrategyUtil#generatorParamByStrategy ParamsGeneratorStrategyUtil.generatorParamByStrategy}
 * Created by houjinxin on 16/6/12.
 */
public class ParamGeneratorScheme {
    private String requestParamName; //请求参数名称
    private boolean needChooseValueSource = false; //是否根据值源表达式确定值的来源,默认为false
    private String valueSourceExpression; //(值源表达式)判定取值来源的逻辑表达式
    private StrategyType strategy; //不需要值源表达式时,取值的策略
    private StrategyType strategyIfTrue; //值源表达式为真时的取值策略
    private StrategyType strategyIfFalse; //值源表达式为假时的取值策略
    private String[] strategyNeedParams = null; //不配置值源表达式时的策略参量
    private String[] strategyNeedParamsIfTrue = null; //值源表达式为真时策略参量
    private String[] strategyNeedParamsIfFalse = null; //值源表达式为假时策略参量
    //满足个性化的需求的配置
    private boolean needIndividualConfig = false; //是否需要个性化配置, 默认为false
    private String individualValueSourceExpression; //个性化值源表达式
    private StrategyType individualStrategy; //个性化取值策略
    private String[] individualStrategyNeedParams; //个性化策略参量

    public String getRequestParamName() {
        return requestParamName;
    }

    public ParamGeneratorScheme setRequestParamName(String requestParamName) {
        this.requestParamName = requestParamName;
        return this;
    }

    public boolean isNeedChooseValueSource() {
        return needChooseValueSource;
    }

    public String getValueSourceExpression() {
        return valueSourceExpression;
    }

    public ParamGeneratorScheme valueSourceExpression(String valueSourceExpression) {
        needChooseValueSource = true; //设置值源表达式后,自动将needChooseValueSource设为true
        this.valueSourceExpression = valueSourceExpression;
        return this;
    }

    public StrategyType getStrategy() {
        return strategy;
    }

    public ParamGeneratorScheme chooseStrategy(StrategyType strategy) {
        if (needChooseValueSource) {
            throw new RuntimeException("需配置值源表达式时,应通过chooseStrategyIfTrue和chooseStrategyIfTrue设置取值策略");
        }
        this.strategy = strategy;
        return this;
    }

    public StrategyType getStrategyIfTrue() {
        return strategyIfTrue;
    }

    public ParamGeneratorScheme chooseStrategyIfTrue(StrategyType strategyIfTrue) {
        if (!needChooseValueSource) {
            throw new RuntimeException("不需要选择值源时,应通过chooseStrategy设置取值策略");
        }
        this.strategyIfTrue = strategyIfTrue;
        return this;
    }

    public StrategyType getStrategyIfFalse() {
        return strategyIfFalse;
    }

    public ParamGeneratorScheme chooseStrategyIfFalse(StrategyType strategyIfFalse) {
        if (!needChooseValueSource) {
            throw new RuntimeException("不需要选择值源时,应通过chooseStrategy设置取值策略");
        }
        this.strategyIfFalse = strategyIfFalse;
        return this;
    }

    public String[] getStrategyNeedParams() {
        return strategyNeedParams;
    }

    public ParamGeneratorScheme configStrategyNeedParams(String... strategyNeedParams) {
        if (needChooseValueSource) {
            throw new RuntimeException("需配置值源表达式时,应通过configStrategyNeedParamsIfTrue或configStrategyNeedParamsIfFalse设置策略参量");
        }
        this.strategyNeedParams = strategyNeedParams;
        return this;
    }

    public String[] getStrategyNeedParamsIfTrue() {
        return strategyNeedParamsIfTrue;
    }

    public ParamGeneratorScheme configStrategyNeedParamsIfTrue(String... strategyNeedParams) {
        if (!needChooseValueSource) {
            throw new RuntimeException("无需配置值源表达式时,应通过configStrategyNeedParams设置策略参量");
        }
        this.strategyNeedParamsIfTrue = strategyNeedParams;
        return this;
    }

    public String[] getStrategyNeedParamsIfFalse() {
        return strategyNeedParamsIfFalse;
    }

    public ParamGeneratorScheme configStrategyNeedParamsIfFalse(String... strategyNeedParamsIfFalse) {
        if (!needChooseValueSource) {
            throw new RuntimeException("无需配置值源表达式时,应通过configStrategyNeedParams设置策略参量");
        }
        this.strategyNeedParamsIfFalse = strategyNeedParamsIfFalse;
        return this;
    }

    public boolean isNeedIndividualConfig() {
        return needIndividualConfig;
    }

    public String getIndividualValueSourceExpression() {
        return individualValueSourceExpression;
    }

    public ParamGeneratorScheme individualValueSourceExpression(String individualValueSourceExpression) {
        needIndividualConfig = true;
        this.individualValueSourceExpression = individualValueSourceExpression;
        return this;
    }

    public StrategyType getIndividualStrategy() {
        return individualStrategy;
    }

    public ParamGeneratorScheme chooseIndividualStrategy(StrategyType individualStrategy) {
        if (!needIndividualConfig) {
            throw new RuntimeException("无个性化配置时,不可以指定个性化取值策略");
        }
        this.individualStrategy = individualStrategy;
        return this;
    }

    public String[] getIndividualStrategyNeedParams() {
        return individualStrategyNeedParams;
    }

    public ParamGeneratorScheme configIndividualStrategyNeedParams(String... strategyNeedParams) {
        if (!needIndividualConfig) {
            throw new RuntimeException("无个性化配置时,不可以指定个性化策略参量");
        }
        this.individualStrategyNeedParams = strategyNeedParams;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
