package com.workflow.engine.core.common.strategy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来描述"请求参数的生成"这一领域的特定语言的语义模型。这个模型中定义了生成请求参数所需要用到的全部参量。
 * 通俗的说,该类代表请求参数生成方案的配置
 * 一个请求对应了多个参数,故这里用ParamGeneratorScheme的列表来表示所有参数的配置方案
 * Created by houjinxin on 16/6/12.
 */
public class ParamGeneratorConfig {

    private List<ParamGeneratorScheme> paramGeneratorSchemeList;

    public ParamGeneratorConfig() {
        paramGeneratorSchemeList = new ArrayList<>();
    }

    public List<ParamGeneratorScheme> getParamGeneratorSchemeList() {
        return paramGeneratorSchemeList;
    }

    public void addParamGeneratorMapping(ParamGeneratorScheme paramGeneratorScheme) {
        getParamGeneratorSchemeList().add(paramGeneratorScheme);
    }

    public int size(){
        return getParamGeneratorSchemeList().size();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
