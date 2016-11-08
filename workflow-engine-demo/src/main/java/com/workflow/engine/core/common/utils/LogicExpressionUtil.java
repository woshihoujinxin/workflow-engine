package com.workflow.engine.core.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * 用于解析字符串形式自定义逻辑表达式,形如:"!condition1&&(condition2||condition3)&&condition4"
 * 这种以字符串方式表示逻辑表达式的目的是让逻辑表达式可以通过配置的方式引入。
 * 比如,在有些时候请求参数的值是根据条件判断得到的,如果以java代码的形式表示为:
 * <blockquote><pre>
 *  if(!condition1) {
 *      a = b;
 *  } else {
 *      a = c;
 *  }
 * </pre></blockquote>
 * <p>
 * 这个代码块转换为字符串逻辑表达式就是"!condition1", 然后通过配置的方式指定当条件为真时参数值的来源{@link
 * } (体现在取值策略和实现该策略所必须的参量)
 * </p>
 * <p>使用方法参见main方法</p>
 *
 * Created by houjinxin on 16/6/1.
 */
public class LogicExpressionUtil {

    /**
     * <pre>
     * 将中缀表达式转为后缀表达式
     * 中缀表达式翻译成后缀表达式的方法如下：
     * （1）从右向左依次取得数据ch。
     * （2）如果ch是操作数，直接输出。
     * （3）如果ch是运算符（含左右括号），则：
     * a：如果ch = '('，放入堆栈。
     * b：如果ch = ')'，依次输出堆栈中的运算符，直到遇到'('为止。
     * c：如果ch不是')'或者'('，那么就和堆栈顶点位置的运算符top做优先级比较。
     * 1：如果ch优先级比top高，那么将ch放入堆栈。
     * 2：如果ch优先级低于或者等于top，那么输出top，然后将ch放入堆栈。
     * （4）如果表达式已经读取完成，而堆栈中还有运算符时，依次由顶端输出。
     * </pre>
     * @param infixExpression 中缀表达式
     * @return 后缀表达式
     */
    public static char[] convertToPostfixExpression(String infixExpression) {
        char[] chars = infixExpression.toCharArray();
        Stack<Character> stack = new Stack<>();
        int j = 0;//afterChars索引
        char[] tempChars = new char[chars.length];
        for (char c : chars) {
            if (isOperator(c)) {//如果是操作符
                if (c == '(') {
                    priorityMap.put(c, 1);
                    stack.push(c);
                } else if (c == ')') {
                    priorityMap.put('(', 4);
                    while (stack.peek() != '(') {
                        tempChars[j++] = stack.pop();
                    }
                    //左括号出栈
                    stack.pop();
                } else {
                    if (stack.size() == 0 || priorityMap.get(c) > priorityMap.get(stack.peek())) {//栈为空或优先级高于栈顶优先级
                        stack.push(c);
                    } else {
                        tempChars[j++] = stack.pop();
                        stack.push(c);
                    }
                }
            } else { //不是操作符直接添加到堆栈
                tempChars[j++] = c;
            }

        }
        while (stack.size() != 0) {
            tempChars[j++] = stack.pop();
        }
        //最终的afterChar数组中元素个数可能小于初始chars的个数,因为括号被舍弃了
        char[] afterChars = new char[j];
        System.arraycopy(tempChars, 0, afterChars, 0, j);
        return afterChars;
    }

    /**
     * 用于根据特定分割符来分割字符串
     *
     * @param str               待分割字符串
     * @param delimiters        分隔符
     * @param trimTokens        是否去掉前后空格
     * @param ignoreEmptyTokens 是否忽略空字符
     * @return
     */
    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    private static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    /**
     * 符号优先级
     */
    private static final Map<Character, Integer> priorityMap = new HashMap<Character, Integer>() {{
        put('!', 3);
        put('&', 2);
        put('|', 2);
        put('(', 4);
    }};

    /**
     * 计算后缀表达式
     * （1）从左向右扫描表达式，一个取出一个数据data
     * （2）如果data是操作数，就压入堆栈
     * （3）如果data是操作符，就从堆栈中弹出此操作符需要用到的数据的个数，进行运算，然后把结果压入堆栈
     * （4）如果数据处理完毕，堆栈中最后剩余的数据就是最终结果。
     *
     * @param postfixExpressionChars
     * @return
     */
    public static boolean calculatePostfixExpression(char[] postfixExpressionChars) {
        Stack<Character> stack = new Stack<>();
        for (char c : postfixExpressionChars) {
            if (!isOperator(c)) {//不是操作符就是操作数
                stack.push(c);
            } else {
                if (c == '!') { //非运算
                    if (stack.pop() == '1') {
                        stack.push('0');
                    } else {
                        stack.push('1');
                    }
                } else {
                    char operand1 = stack.pop();
                    char operand2 = stack.pop();
                    if (c == '&') { //与运算
                        if (operand1 == operand2) {
                            stack.push(operand1);
                        } else {
                            stack.push('0');
                        }
                    } else { //或运算
                        if (operand1 == operand2) {
                            stack.push(operand1);
                        } else {
                            stack.push('1');
                        }
                    }
                }
            }
        }
        return stack.peek() == '1';
    }

    private static boolean isOperator(char c) {
        return "!&|()".contains(String.valueOf(c));
    }

    private static final Map<String, Boolean> _EXPRESSION_JUDGE_RESULT_CACHE = new HashMap<>();
    /**
     * 计算表达式
     * 1、获取表达式中的key,从上下文中获取value, 将key替换为代表真假的0(false)、1(true)。
     * 2、解析逻辑表达式,将中缀表达式转为后缀表达式
     * 3、通过后缀表达式求逻辑表达式值
     * 考虑到有多次计算同一表达式的情况,故将表达式计算的结果缓存起来,若缓存中已经存在该表达式的结果,直接返回
     * 若表达式为"a&b&c",那么缓存key的形式为"a&b&c^V(a)^V(b)^V(c)", 其中V(x)表示x在上下文中的值,
     * 如果上下文中的值发生变化说明缓存失效
     *
     * @param context
     * @param expression
     * @return 求值结果
     */
    private static boolean calculateExpression(Map<String, Object> context, String expression) {
        StringBuilder cacheKeySB = new StringBuilder();
        String expressionBack = expression;
        cacheKeySB.append(expressionBack);
        String[] operands = tokenizeToStringArray(expression, "!&|()", true, false);
        for (String operand : operands) {
            expression = expression.replace(operand, (boolean) context.get(operand) ? "1" : "0").replace("&&", "&").replace("||", "|");
            cacheKeySB.append("^").append(context.get(operand));
        }
        String cacheKey = cacheKeySB.toString();
        boolean expressionValue;

        if (_EXPRESSION_JUDGE_RESULT_CACHE.get(cacheKey) != null) {
            return _EXPRESSION_JUDGE_RESULT_CACHE.get(cacheKey);
        } else {
            expressionValue = calculatePostfixExpression(convertToPostfixExpression(expression));
            _EXPRESSION_JUDGE_RESULT_CACHE.put(cacheKey, expressionValue);
            return expressionValue;
        }
    }

    public static void main(String[] args) {
        Map<String, Object> context = new HashMap<String, Object>() {{
            put("condition1", "2");
            put("condition2", true);
            put("condition3", true);
            put("condition4", true);
        }};

        String expression = "!condition1==2&&(condition2||condition3)&&condition4";
        String[] operands = tokenizeToStringArray(expression, "!&|()", true, false);
        for (String operand : operands) {
            if (operand.contains("==")){
                String left = operand.split("==")[0];
                String right = operand.split("==")[1];
                expression = expression.replace(operand, context.get(left).equals(right) ? "1" : "0").replace("&&", "&").replace("||", "|");
            } else{
                expression = expression.replace(operand, (boolean) context.get(operand) ? "1" : "0").replace("&&", "&").replace("||", "|");
            }
        }
        System.out.println(calculatePostfixExpression(convertToPostfixExpression(expression)));
    }
}
