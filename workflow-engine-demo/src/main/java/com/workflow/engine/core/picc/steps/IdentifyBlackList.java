package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsonHelper;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.utils.JacksonUtil;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import com.workflow.engine.core.common.utils.BusinessUtil;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 被保险人证件号验证
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class IdentifyBlackList implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(IdentifyBlackList.class);

    private static final String _API_UNDER_IDENTIFY_BLACKLIST = "http://www.epicc.com.cn/wap/carProposal/underWrite/identifyBlackList";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_UNDER_IDENTIFY_BLACKLIST, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String resultCode = JacksonUtil.getStringNodeByKey(jsonNode, "resultCode");
        if("1".equals(resultCode)){
            String age = JacksonUtil.getStringNodeByKey(jsonNode, "age");
            String birthday = JacksonUtil.getStringNodeByKey(jsonNode, "birthday");
            String sex = JacksonUtil.getStringNodeByKey(jsonNode, "sex");
            logger.info("被保险人证件验证成功,相关信息为年龄:{}, 生日:{}, 性别:{}", age, birthday, "1".equals(sex) ? "男" : "女");
            context.put("age", age);
            context.put("birthday", birthday);
            context.put("sex", sex);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("被保险人证件验证失败: \n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        String sessionId = (String) context.get("sessionId");
        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        String licenseNo = (String) context.get("licenseNo");
        String engineNo = (String) context.get("engineNo");
        String identify = (String) context.get("identity");
        String frameNo = (String) context.get("frameNo");
        String carOwner = (String) context.get("carOwner");

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("licenseno", licenseNo);
        requestParams.put("engineno", engineNo);
        requestParams.put("frameno", frameNo);
        requestParams.put("identify", identify);
        requestParams.put("licenseflag", "1");
        requestParams.put("identifytype", "01");
        requestParams.put("insuredflag", "0100000");
        requestParams.put("insuredName", carOwner);
        requestParams.put("sessionId", sessionId);
        return requestParams;
    }
}

