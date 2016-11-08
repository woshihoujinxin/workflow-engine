package com.workflow.engine.core.common.flow;

import com.workflow.engine.core.common.StepState;

import java.util.Map;

/**
 * 路由规则:根据前一个步骤的结果来选择下一个步骤,或者是一个步骤或者是一个分支
 * Created by houjinxin on 16/3/9.
 */
public interface IRouter {

    /**
     * 选择下个步骤并执行
     *
     * @param context 上下文
     * @param lastStepState 上个步骤状态
     * @return
     * @throws Exception
     */
    StepState route(Map<String, Object> context, StepState lastStepState) throws Exception;
}
