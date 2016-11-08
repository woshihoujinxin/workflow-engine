package com.workflow.engine.core.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by houjinxin on 16/5/3.
 */
public class Anwser {

    private static final String[] OPERATOR_LIST_ARRAY = {"+", "-", "*", "/", "加", "减", "乘", "除"};
    private static final String[] OPERATOR_MAP_ARRAY = {"\\+:+", "加:+", "-:-", "减:-", "*:*", "乘:*", "/:/", "除:/"};
    private static final String[] OPERAND_MAP_ARRAY = {"零:0", "0:0", "一:1", "壹:1", "1:1", "二:2", "贰:2", "2:2", "三:3", "叁:3", "3:3", "四:4", "肆:4", "4:4", "五:5", "伍:5", "5:5", "六:6", "陆:6", "6:6", "七:7", "柒:7", "7:7", "八:8", "捌:8", "8:8", "九:9", "玖:9", "9:9", "十:10", "拾:10", "10:10"};
    private static final Map<String, String> OPERATOR_MAP;
    private static final Map<String, Integer> OPERAND_MAP;

    static {
        OPERATOR_MAP = new HashMap<String, String>();
        for (String operatorMap : OPERATOR_MAP_ARRAY) {
            OPERATOR_MAP.put(operatorMap.split(":")[0], operatorMap.split(":")[1]);
        }
        OPERAND_MAP = new HashMap<String, Integer>();
        for (String operandMap : OPERAND_MAP_ARRAY) {
            OPERAND_MAP.put(operandMap.split(":")[0], Integer.valueOf(operandMap.split(":")[1]));
        }
    }

    private static String convertOperand(String operand) {
        StringBuffer sb = new StringBuffer();
        for (char operandSnippet : operand.toCharArray()) {
            sb.append(OPERAND_MAP.get(String.valueOf(operandSnippet)));
        }
        return sb.toString();
    }

    //破解问题验证码答案
    public static String decodeValidationCode(String expression) {
        String validationCode = null;
        try {
            String question = "";
            String answer = "";
            Pattern p = Pattern.compile("(.*)(?:等于 |等于|=)(?:.*请选择：)(.*)");
            Matcher y = p.matcher(expression);
            if (y.matches()) {
                question = y.group(1);
                answer = y.group(2);
            }
            String operator = "";
            char[] charArray = question.toCharArray();
            for (char args : charArray) {
                for (String ope : OPERATOR_LIST_ARRAY) {
                    if (ope.equals(String.valueOf(args))) {
                        operator = ope.equals("+") ? "\\"+ope : ope;
                        break;
                    }
                }
                if(!operator.isEmpty()) break;
            }
            Float  result = calculate(convertOperand(question.split(operator)[0]), OPERATOR_MAP.get(operator), convertOperand(question.split(operator)[1]));
            
            
            Map<Float, String> map = new HashMap<>();
            for (String option : answer.split(";")) {
                String[] nameAndValues;
				try {
					nameAndValues = option.trim().split("  ");
					map.put(Float.valueOf(nameAndValues[1]), nameAndValues[0]);
				} catch (Exception e) {
					nameAndValues = option.trim().split(" ");
					map.put(Float.valueOf(nameAndValues[1]), nameAndValues[0]);
				}
                
            }
            validationCode = map.get(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return validationCode;
    }

    public static Float calculate(String left, String operator, String right) {
        Float result;
        Float leftArg = Float.valueOf(left);
        Float rightArg = Float.valueOf(right);
        if (operator.equals("+")) {
            result = leftArg + rightArg;
        } else if (operator.equals("-")) {
            result = leftArg - rightArg;
        } else if (operator.equals("*")) {
            result = leftArg * rightArg;
        } else {
            result = leftArg / rightArg;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(decodeValidationCode("三+叁等于? 请选择：A 5 ; B 6 ; C 7 ; D 8"));
    }
}
