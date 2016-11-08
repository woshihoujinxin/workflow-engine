package com.workflow.engine.core.common.flow;

import com.workflow.engine.core.common.StepState;

import java.util.Map;

/**
 * 表示流程中的一个步骤
 * Created by houjinxin on 16/3/9.
 */
public interface IStep {

    /**
     * 该步骤的具体处理情况由实际情况决定, 步骤执行过程中的任何异常都将被抛出, 以便统一处理异常.
     * @param context 上下文
     * @return 步骤状态
     * @throws Exception
     */
    StepState run(Map<String, Object> context) throws Exception;

}
