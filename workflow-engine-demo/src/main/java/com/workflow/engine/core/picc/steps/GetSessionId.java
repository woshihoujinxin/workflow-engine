package com.workflow.engine.core.picc.steps;


import com.workflow.engine.core.common.utils.JsoupHelper;
import com.workflow.engine.core.service.entity.vo.AreaVO;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.picc.util.PiccBizUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.workflow.engine.core.common.utils.BusinessUtil.executePostMethod;
import static com.workflow.engine.core.common.utils.BusinessUtil.failureStepState;
import static com.workflow.engine.core.common.utils.BusinessUtil.successfulStepState;

/**
 * 获取SessionID
 * Created by houjinxin on 16/3/9.
 */
public class GetSessionId implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(GetSessionId.class);

    private static final String _API_CAR_INPUT1 = "http://www.epicc.com.cn/wap/carProposal/car/carInput1";

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        Map<String, String> requestParams = getRequestParams(context);
        String response = executePostMethod(_API_CAR_INPUT1, requestParams, PiccBizUtil.getHeaders());
        // 获得页面sessionId
        String sessionId = JsoupHelper.getStringById(response, "sessionId");
        if (sessionId != null && !"".equals(sessionId)) {
            logger.info("成功获取到sessionId: {}", sessionId);
            context.put("sessionId", sessionId);
            return successfulStepState(this);
        } else {
            logger.info("无法获取到sessionId, 流程终止. 当前步骤请求的响应为:\n{}", response);
            return failureStepState(this);
        }
    }

    public Map<String, String> getRequestParams(Map<String, Object> context) {
        //获取保险公司区域相关代码,一般在流程的第一步中设置
        Map<String, Map<String, String>> cityCodeMappings = (Map<String, Map<String, String>>) context.get("cityCodeMapping");
        AreaVO area = (AreaVO) context.get("area");
        Map<String, String> cityCodeAndAreaCodeMapping = cityCodeMappings.get(area.getCityCode());
        context.put("cityCode", cityCodeAndAreaCodeMapping.get("cityCode")); //保险公司CityCode
        context.put("provinceCode", cityCodeAndAreaCodeMapping.get("areaCode"));//保险公司provinceCode

        String proSelected = (String) context.get("provinceCode");
        String citySelected = (String) context.get("cityCode");
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("head.requestType", "");
        requestParams.put("head.requestCode", "20132001");
        requestParams.put("head.uuid", "1234");
        requestParams.put("head.sessionId", "first");
        requestParams.put("head.channelNo", "2");
        requestParams.put("proSelected", proSelected);
        requestParams.put("citySelected", citySelected);
        requestParams.put("citySelect", "");
        requestParams.put("netAddress", "");
        requestParams.put("carInfo.ccaFlag", "");
        requestParams.put("carInfo.ccaEntryId", "");
        return requestParams;
    }
}
