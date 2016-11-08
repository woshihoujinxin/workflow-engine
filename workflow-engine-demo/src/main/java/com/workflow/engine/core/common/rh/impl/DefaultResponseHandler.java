package com.workflow.engine.core.common.rh.impl;

import com.workflow.engine.core.common.Constants;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.rh.IResponseHandler;

import java.util.Map;

/**
 * 默认响应处理器
 * Created by houjinxin on 16/3/11.
 */
public class DefaultResponseHandler implements IResponseHandler {

    @Override
    public StepState handleResponse(Map<String, Object> context, IStep step, Object response) {
        return new StepState(step.getClass().getName(), Constants._FLOW_CONTINUE_FLAG, Constants._STEP_SUCCESS_FLAG);
    }

    @Override
    public StepState handleResponse(Map<String, Object> context, IStep step, Object response, Object... others) {
        return handleResponse(context, step, response);
    }
}
