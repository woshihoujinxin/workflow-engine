package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.Tuple2;
import com.workflow.engine.core.common.flow.IFlow;
import com.workflow.engine.core.common.flow.IRouter;
import com.workflow.engine.core.common.flow.IStep;

import java.util.Map;


public class Flow implements IFlow {

    //元组的第一个元素可能是IStep或IFlow接口的实现,故用Object类型
    private Tuple2<Object, IRouter> firstStepWithRouter;

    public Flow(Tuple2<Object, IRouter> firstStepWithRouter){
        this.firstStepWithRouter = firstStepWithRouter;
    }

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        IStep step = (IStep) firstStepWithRouter.getFirst();
        IRouter router = firstStepWithRouter.getSecond();
        StepState firstStepState = step.run(context);
        return router.route(context, firstStepState);
    }
}
