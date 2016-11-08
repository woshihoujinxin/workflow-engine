package com.workflow.engine.expression.op;

import com.workflow.engine.expression.IllegalExpressionException;
import com.workflow.engine.expression.datameta.BaseDataMeta;
import com.workflow.engine.expression.datameta.Constant;

/**
 * 操作符执行接口
 *
 * @author 林良益，卓诗垚
 * @version 2.0
 *          2008-09-26
 */
public interface IOperatorExecution {
    /**
     * 执行操作符运算
     *
     * @param args 注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
     * @return Constant 常量型的执行结果
     * @throws IllegalExpressionException
     */
    public Constant execute(Constant[] args) throws IllegalExpressionException;

    /**
     * 验证操作符参数是否合法
     *
     * @param opPosition
     * @param args       注意args中的参数由于是从栈中按LIFO顺序弹出的，所以必须从尾部倒着取数
     * @return Constant 常量型的执行结果
     * @throws IllegalExpressionException
     */
    public Constant verify(int opPosition, BaseDataMeta[] args) throws IllegalExpressionException;
}
