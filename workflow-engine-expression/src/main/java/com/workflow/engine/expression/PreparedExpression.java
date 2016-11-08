package com.workflow.engine.expression;

import com.workflow.engine.expression.datameta.Constant;
import com.workflow.engine.expression.datameta.Variable;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预编译的表达式
 * 采用预编译策略，对表达式相同，但参数不同的计算，有效的优化执行效率
 *
 * @author 林良益
 * @version 2.0
 *          2009-09-05
 */
public class PreparedExpression {

    //原始表达式字符
    private String orgExpression;

    //编译验证后生成的RPN
    private List<ExpressionToken> expTokens;

    //编译验证后生成的表达式的变量表
    private Map<String, Variable> variableMap;

    /**
     * 构造函数
     *
     * @param expTokens
     */
    PreparedExpression(String orgExpression, List<ExpressionToken> expTokens, Map<String, Variable> variableMap) {
        this.orgExpression = orgExpression;
        this.expTokens = expTokens;
        this.variableMap = new HashMap<String, Variable>(variableMap);
    }

    /**
     * 设置指定参数的值
     * 如果参数不存在，则抛出IllegalArgumentException运行时异常
     *
     * @param name  参数名
     * @param value 参数值
     */
    public synchronized void setArgument(String name, Object value) {
        Variable v = variableMap.get(name);
        if (v != null) {
            v.setVariableValue(value);
        } else {
            throw new IllegalArgumentException("无法识别的表达式参数：" + name);
        }
    }

    /**
     * 执行当前的预编译表达式
     *
     * @return
     */
    public Object execute() {
        //执行之前必须先绑定表达式上下文
        ExpressionExecutor ee = new ExpressionExecutor(new ExpressionContext() {{
            putAll(variableMap);
        }});
        //执行RPN
        try {
            Constant constant = ee.execute(expTokens);
            return constant.toJavaObject();
        } catch (IllegalExpressionException e) {
            e.printStackTrace();
            throw new RuntimeException("表达式：\"" + orgExpression + "\" 执行异常");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("表达式：\"" + orgExpression + "\" 执行异常");
        } finally {
        }
    }

    @Override
    public String toString() {
        StringBuffer expression = new StringBuffer();
        for (ExpressionToken expToken : expTokens) {
            if (expToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION) {
                expression.append("$").append(expToken.toString()).append(" ");
            } else {
                expression.append(expToken.toString()).append(" ");
            }
        }
        expression.delete(expression.length() - 1, expression.length());
        return expression.toString();
    }
}
