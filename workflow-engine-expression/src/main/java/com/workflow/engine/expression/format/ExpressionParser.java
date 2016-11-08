package com.workflow.engine.expression.format;

import com.workflow.engine.expression.ExpressionToken;
import com.workflow.engine.expression.datameta.BaseDataMeta;
import com.workflow.engine.expression.op.Operator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author 林良益，卓诗垚
 * @version 2.0
 *          Sep 23, 2008
 */
public class ExpressionParser {

    private static Map<String, Operator> operators = new HashMap<String, Operator>();

    static {
        operators.put(Operator.NOT.getToken(), Operator.NOT);

        //	operators.put("-", NG); 负号和减号的差异通过上下文区分
        operators.put(Operator.MUTI.getToken(), Operator.MUTI);
        operators.put(Operator.DIV.getToken(), Operator.DIV);
        operators.put(Operator.MOD.getToken(), Operator.MOD);

        operators.put(Operator.PLUS.getToken(), Operator.PLUS);
        operators.put(Operator.MINUS.getToken(), Operator.MINUS);


        operators.put(Operator.LT.getToken(), Operator.LT);
        operators.put(Operator.LE.getToken(), Operator.LE);
        operators.put(Operator.GT.getToken(), Operator.GT);
        operators.put(Operator.GE.getToken(), Operator.GE);

        operators.put(Operator.EQ.getToken(), Operator.EQ);
        operators.put(Operator.NEQ.getToken(), Operator.NEQ);

        operators.put(Operator.AND.getToken(), Operator.AND);

        operators.put(Operator.OR.getToken(), Operator.OR);

        operators.put(Operator.APPEND.getToken(), Operator.APPEND);

        operators.put(Operator.SELECT.getToken(), Operator.SELECT);
        operators.put(Operator.QUES.getToken(), Operator.QUES);
        operators.put(Operator.COLON.getToken(), Operator.COLON);

    }

    /**
     * 通过名称取得操作符
     *
     * @param name
     * @return
     */
    public Operator getOperator(String name) {
        return operators.get(name);
    }

    private Stack<String> parenthesis = new Stack<String>();//匹配圆括号的栈

    public List<ExpressionToken> getExpressionTokens(String expression) throws FormatException {
        ExpressionReader eReader = new ExpressionReader(expression);
        List<ExpressionToken> list = new ArrayList<ExpressionToken>();
        ExpressionToken expressionToken = null;//上一次读取的ExpressionToken
        Element ele = null;
        try {
            while ((ele = eReader.readToken()) != null) {
                expressionToken = changeToToken(expressionToken, ele);
                //如果是括号，则记录下来，最后进行最后进行匹配
                pushParenthesis(ele);
                list.add(expressionToken);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new FormatException("表达式词元格式异常");
        }
        if (!parenthesis.isEmpty()) {
            throw new FormatException("括号匹配出错");
        }

        return list;
    }

    /**
     * 如果是括号，则记录下来，最后进行最后进行匹配
     *
     * @param ele
     * @throws FormatException
     */
    public void pushParenthesis(Element ele) throws FormatException {
        if (Element.ElementType.SPLITTER == ele.getType()) {
            if (ele.getText().equals("(")) {
                parenthesis.push("(");
            } else if (ele.getText().equals(")")) {
                if (parenthesis.isEmpty() || !parenthesis.peek().equals("(")) {
                    throw new FormatException("括号匹配出错");
                } else {
                    parenthesis.pop();
                }
            }
        }
    }

    /**
     * 将切分的元素转化成ExpressionToken，并验证"-"是减号还是负号
     *
     * @param previousToken
     * @param ele
     * @return
     * @throws ParseException
     */
    public ExpressionToken changeToToken(ExpressionToken previousToken, Element ele) throws ParseException {
        if (ele == null) {
            throw new IllegalArgumentException();
        }
        ExpressionToken token = null;

        //转成ExpressionToken
        if (Element.ElementType.NULL == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_NULL, null);
        } else if (Element.ElementType.STRING == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_STRING, ele.getText());
        } else if (Element.ElementType.BOOLEAN == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_BOOLEAN, Boolean.valueOf(ele.getText()));
        } else if (Element.ElementType.INT == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_INT, Integer.valueOf(ele.getText()));
        } else if (Element.ElementType.LONG == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_LONG, Long.valueOf(ele.getText()));
        } else if (Element.ElementType.FLOAT == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_FLOAT, Float.valueOf(ele.getText()));
        } else if (Element.ElementType.DOUBLE == ele.getType()) {
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_DOUBLE, Double.valueOf(ele.getText()));
        } else if (Element.ElementType.DATE == ele.getType()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_DATE, sdf.parse(ele.getText()));
        } else if (Element.ElementType.VARIABLE == ele.getType()) {
            token = ExpressionToken.createVariableToken(ele.getText());
        } else if (Element.ElementType.OPERATOR == ele.getType()) {
            //区分负号
            if (ele.getText().equals("-") && (
                    previousToken == null //以“-”开头肯定是负号
                            || previousToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR //运算符后面肯定是负号
                            || previousToken.getTokenType() == ExpressionToken.ETokenType.ETOKEN_TYPE_SPLITTER //“(”或“,”后面肯定是负号
                            && !")".equals(previousToken.getSplitter())
            )) {
                token = ExpressionToken.createOperatorToken(Operator.NG);
            } else {
                token = ExpressionToken.createOperatorToken(getOperator(ele.getText()));
            }
        } else if (Element.ElementType.FUNCTION == ele.getType()) {
            token = ExpressionToken.createFunctionToken(ele.getText());
        } else if (Element.ElementType.SPLITTER == ele.getType()) {
            token = ExpressionToken.createSplitterToken(ele.getText());
        } else if (Element.ElementType.CLASS == ele.getType()){
            token = ExpressionToken.createCastTypeToken(ele.getText());
        }
        token.setStartPosition(ele.getIndex());

        return token;
    }


    public static void main(String[] s) {
        String expression = "$CALCDATE($SYSDATE() ,0,0 , 7,0 ,0,aa ) > [2008-10-01]";
//        String expression = "@Boolean$CONTAINS(\"aaa\",\"a\")";
        ExpressionParser ep = new ExpressionParser();

        try {
            List<ExpressionToken> list = ep.getExpressionTokens(expression);

            for (ExpressionToken et : list) {
                System.out.println(et.getTokenType() + " : " + et.toString());
            }
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }
}
