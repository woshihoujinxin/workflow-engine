package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.Tuple2;
import com.workflow.engine.core.common.flow.IRouter;

/**
 * 抽象Router
 * Created by houjinxin on 16/3/9.
 */
public abstract class AbstractRouter implements IRouter {

    protected Tuple2 nextStepWithRouter;

    public AbstractRouter(Tuple2 nextStepWithRouter){
        this.nextStepWithRouter = nextStepWithRouter;
    }

}
