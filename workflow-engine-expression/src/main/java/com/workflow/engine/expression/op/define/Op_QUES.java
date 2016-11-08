package com.workflow.engine.expression.op.define;

import com.workflow.engine.expression.IllegalExpressionException;
import com.workflow.engine.expression.datameta.BaseDataMeta;
import com.workflow.engine.expression.datameta.Constant;
import com.workflow.engine.expression.op.IOperatorExecution;
import com.workflow.engine.expression.op.Operator;

/**
 * @author 林良益，卓诗垚
 * @version 2.0
 *          2009-02-06
 */
public class Op_QUES implements IOperatorExecution {

    public static final Operator THIS_OPERATOR = Operator.QUES;

    @Override
    public Constant execute(Constant[] args) {
        throw new UnsupportedOperationException("操作符\"" + THIS_OPERATOR.getToken() + "不支持该方法");
    }

    @Override
    public Constant verify(int opPosition, BaseDataMeta[] args)
            throws IllegalExpressionException {
        throw new UnsupportedOperationException("操作符\"" + THIS_OPERATOR.getToken() + "不支持该方法");
    }

}
