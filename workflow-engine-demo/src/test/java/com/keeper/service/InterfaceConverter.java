package com.keeper.service;

import org.databene.benerator.anno.Source;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by houjinxin on 16/7/19.
 */
@RunWith(MyFeeder.class)
@ContextConfiguration(locations = {"classpath:spring-service.xml"})
@Transactional
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
public class InterfaceConverter {

    private static List<Map<String, String>> paramsInfo = new ArrayList<>();

    private static Map<String, String> typeMappings = new HashMap<String, String>() {{
        put("Int", "integer");
        put("Double", "number");
        put("String", "string");
        put("int", "integer");
        put("double", "number");
        put("string", "string");
    }};
    private static Map<String, String> typeFormatMappings = new HashMap<String, String>() {{
        put("integer", "int32");
        put("number", "double");
        put("string", "");
    }};
    private String PARAMS_YAMLTEMPLATE = "parameters:\n";
    private String PARAM_YAMLTEMPLATE =
            "        - name: ${paramName}\n" +
                    "          in: query\n" +
                    "          description: ${description}\n" +
                    "          required: ${require}\n" +
                    "          type: ${type}\n";
    private String FORMAT_YAMLTEMPLATE =
            "          format: ${format}\n";

    @Test
    @Source(value = "接口文档.xlsx")
    public void test(final String paramName, final String require, final String type, final String description) {
        Map<String, String> paramInfo = null;
        if (!paramName.equals("")){
            paramInfo = new HashMap<>();
            paramInfo.put("paramName", removeChineseInParam(paramName));
            paramInfo.put("type", typeMappings.get(type.trim()));
            paramInfo.put("require", require.trim().equals("是") ? "true" : "false");
            paramInfo.put("description", description.trim());
            paramsInfo.add(paramInfo);
        }
        if (paramName.equals("SecCode")) {
            convertParamsInfoToParameterObject();
        }
    }

    private void convertParamsInfoToParameterObject() {
        StringBuilder accum = new StringBuilder();
        accum.append(PARAMS_YAMLTEMPLATE);
        String targetStr = PARAM_YAMLTEMPLATE;
        for (Map<String, String> paramInfo : paramsInfo) {
//            System.out.println(paramInfo);
            for (Map.Entry<String, String> entry : paramInfo.entrySet()) {
                targetStr = replaceTemplate(targetStr, entry.getKey(), entry.getValue());
//                System.out.println(targetStr);
                if (entry.getKey().equals("type") && !entry.getValue().equals("string")) {
                    targetStr += replaceTemplate(FORMAT_YAMLTEMPLATE, "format", typeFormatMappings.get(entry.getValue()));
                }
            }
            accum.append(targetStr);
            targetStr = PARAM_YAMLTEMPLATE;
        }
        System.out.println(accum.toString());
    }

//    @Test
    public void testReplace() {
        String x = PARAM_YAMLTEMPLATE;
        x = replaceTemplate(x, "paramName", "paramName1");
        System.out.println(x);
        x = replaceTemplate(x, "description", "description1");
        System.out.println(x);
        x = replaceTemplate(x, "require", "require1");
        System.out.println(x);
        x = replaceTemplate(x, "type", "type1");
        System.out.println(x);
        x = replaceTemplate(x, "format", "format1");
        System.out.println(x);
    }

    public static String replaceTemplate(String templateStr, String name, String value) {
        Pattern pattern = Pattern.compile("(?<=\\$\\{)[^}]+");
        Matcher matcher = pattern.matcher(templateStr);
        while (matcher.find()) {
            if (name.equals(matcher.group())) {
                templateStr = templateStr.replace("${" + matcher.group() + "}", value);
            }
        }
        return templateStr;
    }

//    @Test
    public void testRemoveChinese(){
        removeChineseInParam("CarUsedType（修改）");
    }
    public static String removeChineseInParam(String param){
        Pattern pattern = Pattern.compile(".*(（.*）)");
        Matcher matcher = pattern.matcher(param);
        if (matcher.matches()) {
            param = param.replace(matcher.group(1), "");
            System.out.println(param);
        }
        return param;
    }
}
