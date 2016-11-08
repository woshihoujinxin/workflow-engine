package com.workflow.engine.core.pingan.steps;


import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.workflow.engine.core.common.utils.StepUtil.handleResponse;

/**
 * 构造Quote对象
 * Created by houjinxin on 16/3/9.
 */
public class CreateQuote implements IStep {

    private static final Logger logger = LoggerFactory.getLogger(CreateQuote.class);

    @Override
    public StepState run(Map<String, Object> context) throws Exception {
        return handleResponse(context, this, null);
    }

}

