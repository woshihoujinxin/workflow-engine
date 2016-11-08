package com.workflow.engine.expression.op.define;


import com.workflow.engine.expression.IllegalExpressionException;
import com.workflow.engine.expression.datameta.BaseDataMeta;
import com.workflow.engine.expression.datameta.Constant;
import com.workflow.engine.expression.datameta.Reference;
import com.workflow.engine.expression.op.IOperatorExecution;
import com.workflow.engine.expression.op.Operator;

/**
 * 算术取负操作
 *
 * @author 林良益，卓诗垚
 * @version 2.0
 *          2008-09-26
 */
public class Op_NG implements IOperatorExecution {

    public static final Operator THIS_OPERATOR = Operator.NG;

    @Override
    public Constant execute(Constant[] args) throws IllegalExpressionException {

        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "参数个数不匹配");
        }

        Constant first = args[0];
        if (null == first || null == first.getDataValue()) {
            //抛NULL异常
            throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
        }

        //如果第一参数为引用，则执行引用
        if (first.isReference()) {
            Reference firstRef = (Reference) first.getDataValue();
            first = firstRef.execute();
        }

        if (BaseDataMeta.DataType.DATATYPE_DOUBLE == first.getDataType()) {
            Double result = 0 - first.getDoubleValue();
            return new Constant(BaseDataMeta.DataType.DATATYPE_DOUBLE, result);

        } else if (BaseDataMeta.DataType.DATATYPE_FLOAT == first.getDataType()) {
            Float result = 0 - first.getFloatValue();
            return new Constant(BaseDataMeta.DataType.DATATYPE_FLOAT, result);

        } else if (BaseDataMeta.DataType.DATATYPE_LONG == first.getDataType()) {
            Long result = 0 - first.getLongValue();
            return new Constant(BaseDataMeta.DataType.DATATYPE_LONG, result);

        } else if (BaseDataMeta.DataType.DATATYPE_INT == first.getDataType()) {
            Integer result = 0 - first.getIntegerValue();
            return new Constant(BaseDataMeta.DataType.DATATYPE_INT, result);

        } else {
            //抛异常
            throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数类型错误");

        }

    }

    @Override
    public Constant verify(int opPosition, BaseDataMeta[] args)
            throws IllegalExpressionException {

        if (args == null) {
            throw new IllegalArgumentException("运算操作符参数为空");
        }
        if (args.length != 1) {
            //抛异常
            throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数个数不匹配"
                    , THIS_OPERATOR.getToken()
                    , opPosition
            );
        }

        BaseDataMeta first = args[0];
        if (first == null) {
            throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
        }

        if (BaseDataMeta.DataType.DATATYPE_DOUBLE == first.getDataType()) {
            return new Constant(BaseDataMeta.DataType.DATATYPE_DOUBLE, Double.valueOf(0.0));

        } else if (BaseDataMeta.DataType.DATATYPE_FLOAT == first.getDataType()) {
            return new Constant(BaseDataMeta.DataType.DATATYPE_FLOAT, Float.valueOf(0F));

        } else if (BaseDataMeta.DataType.DATATYPE_LONG == first.getDataType()) {
            return new Constant(BaseDataMeta.DataType.DATATYPE_LONG, Long.valueOf(0l));

        } else if (BaseDataMeta.DataType.DATATYPE_INT == first.getDataType()) {
            return new Constant(BaseDataMeta.DataType.DATATYPE_INT, Integer.valueOf(0));

        } else {
            //抛异常
            throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数类型错误"
                    , THIS_OPERATOR.getToken()
                    , opPosition
            );

        }
    }

}
