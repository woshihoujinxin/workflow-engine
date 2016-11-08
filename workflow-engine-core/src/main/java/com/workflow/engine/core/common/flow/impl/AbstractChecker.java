package com.workflow.engine.core.common.flow.impl;

import com.workflow.engine.core.common.flow.IChecker;

public abstract class AbstractChecker<T> implements IChecker<T> {

    protected T except;

    public AbstractChecker(T except) {
        this.except = except;
    }

}
