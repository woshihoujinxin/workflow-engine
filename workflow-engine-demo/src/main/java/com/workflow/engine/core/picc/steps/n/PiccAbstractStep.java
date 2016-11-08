package com.workflow.engine.core.picc.steps.n;

import com.workflow.engine.core.common.flow.impl.AbstractStep;
import com.workflow.engine.core.picc.util.PiccBizUtil;

import java.util.Map;

/**
 * Created by houjinxin on 16/6/3.
 */
public abstract class PiccAbstractStep extends AbstractStep {

    @Override
    protected Map<String, String> getHeaders() {
        return PiccBizUtil.getHeaders();
    }
}
