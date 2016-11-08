package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.Tuple2;
import com.workflow.engine.core.common.flow.IChecker;
import com.workflow.engine.core.common.flow.IRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static com.workflow.engine.core.common.Constants._FLOW_CONTINUE_FLAG;

/**
 * 条件路由规则: 若下一步骤是由多个分支组成的map,则根据map的key选择分支子流程
 * Created by houjinxin on 16/3/9.
 */
public class ConditionRouter extends AbstractRouter {

    private static Logger logger = LoggerFactory.getLogger(ConditionRouter.class);

    public ConditionRouter(Tuple2 nextStepWithRouter) {
        super(nextStepWithRouter);
    }

    @Override
    public StepState route(Map<String, Object> context, StepState lastStepState) throws Exception {
        StepState currentStepState;
        if (_FLOW_CONTINUE_FLAG == lastStepState.getContinueFlag() && nextStepWithRouter != null) {
            Map<IChecker<String>, Flow> flows = (Map<IChecker<String>, Flow>) nextStepWithRouter.getFirst();
            IRouter router = (IRouter) nextStepWithRouter.getSecond();
            //寻找与当前StepState的forkFlag相符的分支
            Flow forkFlow = null;
            Set<Map.Entry<IChecker<String>, Flow>> entrySet = flows.entrySet();
            for (Map.Entry<IChecker<String>, Flow> entry : entrySet) {
                IChecker checker = entry.getKey();
                if (checker.check(lastStepState.getForkFlag())) {
                    forkFlow = entry.getValue();
                    if (logger.isDebugEnabled()) {
                        logger.debug("正在执行步骤名成为【{}】后的分支，分支标识为【{}】", lastStepState.getStepName(), lastStepState.getForkFlag());
                    }
                    break;
                }
            }
            //若子分支存在执行流程
            if (forkFlow == null) {
                logger.info("步骤名称为[" + lastStepState.getStepName() + "]的step, forkFlag参数未指定有效值,故跳过分支执行后续步骤");
                currentStepState = lastStepState;
            } else {
                currentStepState = forkFlow.run(context);
            }
            //递归执行后续步骤
            return router.route(context, currentStepState);
        } else {
            currentStepState = lastStepState;
        }
        return currentStepState;
    }
}
