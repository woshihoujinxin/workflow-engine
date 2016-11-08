package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.Tuple2;
import com.workflow.engine.core.common.flow.IRouter;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 简单路由规则:
 * 如果"继续",就直接调用下一个step
 * Created by houjinxin on 16/3/9.
 */
public class SimpleRouter extends AbstractRouter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRouter.class);

    public SimpleRouter(Tuple2 nextStepWithRouter) {
        super(nextStepWithRouter);
    }

    @Override
    public StepState route(Map<String, Object> context, StepState lastStepState) throws Exception {
        StepState currentStepState ;
        if (Constants._FLOW_CONTINUE_FLAG == lastStepState.getContinueFlag() && nextStepWithRouter != null) {
            IStep step = (IStep) nextStepWithRouter.getFirst();
            if (logger.isDebugEnabled()) {
                logger.debug("当前执行步骤是【{}】", step.getClass().getSimpleName());
            }
            IRouter router = (IRouter) nextStepWithRouter.getSecond();
            currentStepState = step.run(context);
            //递归执行后续步骤
            return router.route(context, currentStepState);
        } else {
            currentStepState = lastStepState;
        }
        return currentStepState;
    }
}
