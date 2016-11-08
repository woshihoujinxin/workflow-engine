package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IFlow;
import com.workflow.engine.core.common.Constants;

import java.util.LinkedList;
import java.util.Map;

/**
 * 流程链定义: 多个流程链可以组成一个更复杂的流程
 * Created by houjinxin on 16/3/10.
 */
public class FlowChain implements IFlow {

    private LinkedList<Flow> flows;

    public FlowChain(LinkedList<Flow> flows) {
        this.flows = flows;
    }

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        //preStepState代表上一个流程链的结果,这里设置初始值目的是默认让流程链可执行
        StepState preStepState = new StepState("default", Constants._FLOW_CONTINUE_FLAG, Constants._STEP_SUCCESS_FLAG);
        for (Flow flow : flows) {
            preStepState = executeFlowChain(preStepState, flow, context);
            if (preStepState.getContinueFlag() == Constants._FLOW_FINISHED_FLAG) {
                break;
            }
        }
        return preStepState;
    }

    /**
     * 根据前一个流程的结束状态,确定流程链是否继续
     *
     * @param preStepState 前一个流程执行的结束状态
     * @param flow         当前流程
     * @param context      上下文
     * @return
     */
    private StepState executeFlowChain(StepState preStepState, Flow flow, Map<String, Object> context) throws Exception {
        StepState stepState;
        if (preStepState.getContinueFlag() == Constants._FLOW_CONTINUE_FLAG) {
            stepState = flow.run(context);
        } else {
            stepState = preStepState;
        }
        return stepState;
    }
}
