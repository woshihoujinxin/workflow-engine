package com.workflow.engine.core.pingan.steps.n;

import com.workflow.engine.core.common.flow.impl.AbstractStep;
import com.workflow.engine.core.pingan.utils.PinganBizUtil;

import java.util.Map;

/**
 * Created by houjinxin on 16/6/8.
 */
public abstract class PinganAbstractStep extends AbstractStep {

    @Override
    protected Map<String, String> getHeaders() {
        return PinganBizUtil.getHeaders();
    }

}
