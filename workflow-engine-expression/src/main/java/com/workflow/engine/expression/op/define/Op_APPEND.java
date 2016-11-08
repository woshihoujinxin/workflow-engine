package com.workflow.engine.expression.op.define;

import com.workflow.engine.expression.IllegalExpressionException;
import com.workflow.engine.expression.datameta.BaseDataMeta;
import com.workflow.engine.expression.datameta.Constant;
import com.workflow.engine.expression.datameta.Reference;
import com.workflow.engine.expression.op.IOperatorExecution;
import com.workflow.engine.expression.op.Operator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 集合添加操作
 *
 * @author 林良益，卓诗垚
 * @version 2.0
 *          Oct 8, 2008
 */
public class Op_APPEND implements IOperatorExecution {

    public static final Operator THIS_OPERATOR = Operator.APPEND;

    @Override
    public Constant execute(Constant[] args) throws IllegalExpressionException {

        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "参数个数不匹配");
        }
        Constant first = args[1];
        Constant second = args[0];
        if (first == null || second == null) {
            throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
        }
        //如果第一参数为引用，则执行引用
        if (first.isReference()) {
            Reference firstRef = (Reference) first.getDataValue();
            first = firstRef.execute();
        }
        //如果第二参数为引用，则执行引用
        if (second.isReference()) {
            Reference secondRef = (Reference) second.getDataValue();
            second = secondRef.execute();
        }
        return append(first, second);
    }

    @Override
    public Constant verify(int opPosition, BaseDataMeta[] args)
            throws IllegalExpressionException {

        if (args == null) {
            throw new IllegalArgumentException("运算操作符参数为空");
        }
        if (args.length != 2) {
            //抛异常
            throw new IllegalExpressionException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数个数不匹配"
                    , THIS_OPERATOR.getToken()
                    , opPosition
            );
        }
        BaseDataMeta first = args[1];
        BaseDataMeta second = args[0];

        if (first == null || second == null) {
            throw new NullPointerException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数为空");
        }
        //APPEND接受任何类型的参数，总是返回Collection类型的常量
        return new Constant(BaseDataMeta.DataType.DATATYPE_LIST, null);

    }

    /**
     * 合并两个常量对象
     *
     * @param arg1
     * @param arg2
     * @return
     */
    private Constant append(Constant arg1, Constant arg2) {
        if (arg1 == null || arg2 == null) {
            throw new IllegalArgumentException("操作符\"" + THIS_OPERATOR.getToken() + "\"参数丢失");
        }
        List<Object> resultCollection = new ArrayList<Object>();
        //合并参数一
        merge(resultCollection, arg1);
        //合并参数二
        merge(resultCollection, arg2);
        //构造新的collection 常量
        return new Constant(BaseDataMeta.DataType.DATATYPE_LIST, resultCollection);
    }

    private void merge(List<Object> resultCollection, Constant arg) {
        if (BaseDataMeta.DataType.DATATYPE_LIST == arg.getDataType()) {
            if (arg.getCollection() != null) {
                resultCollection.addAll(arg.getCollection());
            }
        } else {
            try {
                Object object = arg.toJavaObject();
                resultCollection.add(object);
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }
    }

}
