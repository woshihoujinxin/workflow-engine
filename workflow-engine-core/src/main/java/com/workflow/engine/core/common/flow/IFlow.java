package com.workflow.engine.core.common.flow;

import com.workflow.engine.core.common.StepState;

import java.util.Map;

/**
 * 流程定义
 * 用于表示一个流程,提供运转流程的方法
 * Created by houjinxin on 16/3/9.
 */
public interface IFlow {

    /**
     * 运行流程, 抛出异常
     * @param context 流转过程中使用的上下文，包括一些必要参数，常量，方便取值
     * @return
     * @throws Exception 任何异常
     */
    StepState run(Map<String, Object> context) throws Exception;
}
