package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.HttpHelper;
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


/**
 * 快速报价获取验证码以及识别验证码
 * Created by houjinxin on 16/3/9.
 */
@Deprecated
public class VerifyCaptcha implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCaptcha.class);

    private static final String _API_CREATE_IMAGE = "http://www.epicc.com.cn/wap/CreateImage";
    private static final String _API_VERIFY_CODE_CHECK = "http://www.epicc.com.cn/wap/personelCenter/customer/verifyCodeCheck";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        String filePath = "";
        String fileName = "";
        HttpHelper.downImage(_API_CREATE_IMAGE, filePath, fileName);
        //TODO: 识别验证码
        Map<String, String> requestParams = getRequestParams(context);
        String response = BusinessUtil.executePostMethod(_API_VERIFY_CODE_CHECK, requestParams, PiccBizUtil.getHeaders());
        JsonNode jsonNode = JacksonUtil.getJsonNode(JsonHelper.convert(response));
        String status = JacksonUtil.getStringNodeByKey(jsonNode, "status");
        if ("success".equals(status)) {
            logger.info("验证码识别成功\n{}", response);
            return BusinessUtil.successfulStepState(this);
        } else {
            logger.info("验证码识别失败,响应为:\n{}", response);
            return BusinessUtil.failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {

        String checkCode = ""; //识别软件识别结果
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("signOnForm", checkCode);
        return requestParams;
    }
}

