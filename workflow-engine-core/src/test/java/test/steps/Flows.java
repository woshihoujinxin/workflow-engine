package test.steps;

import com.workflow.engine.core.common.Constants;
import com.workflow.engine.core.common.FlowBuilder;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.flow.impl.Flow;
import com.workflow.engine.core.common.flow.impl.SimpleRouter;
import com.workflow.engine.core.common.utils.FlowBuilderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.workflow.engine.core.common.Constants._FLOW_CONTINUE_FLAG;
import static com.workflow.engine.core.common.Constants._STEP_SUCCESS_FLAG;

/**
 * 流程与步骤配置
 * 可以配置子流程,并用子流程拼装成流程链
 * Created by houjinxin on 16/3/9.
 */
public class Flows {

    private static Logger logger = LoggerFactory.getLogger(Flows.class);

    private static final String _FLOW_FILE_PATH = "/Users/houjinxin/Documents/workflow-engine/workflow-engine-core/src/test/resources/test.flows";

    public static final Map<String, Class<? extends IStep>> _NAME_STEP_CLAZZ_MAPPINGS = new HashMap<String, Class<? extends IStep>>() {{
        put("step1", Step1.class);
        put("step2", Step2.class);
        put("step3", Step3.class);
    }};

    public static final LinkedList<Object> _FLOW_STEP_ORDER_CONFIG = FlowBuilderUtil.extractSteps(_FLOW_FILE_PATH);

    public static final Flow _FLOW = new FlowBuilder(_NAME_STEP_CLAZZ_MAPPINGS, _FLOW_STEP_ORDER_CONFIG).build();

    /**
     * Created by houjinxin on 2016/11/8.
     */
    public static class Step1 implements IStep {
        @Override
        public StepState run(Map<String, Object> context) throws Exception {
            context.put("step1-value","value1");
            return new StepState(this.getClass().getName(), _FLOW_CONTINUE_FLAG, _STEP_SUCCESS_FLAG, "true");
        }
    }

    /**
     * Created by houjinxin on 2016/11/8.
     */
    public static class Step2 implements IStep {
        @Override
        public StepState run(Map<String, Object> context) throws Exception {
            context.put("step3-value","value1");
            return new StepState(this.getClass().getName(), _FLOW_CONTINUE_FLAG, _STEP_SUCCESS_FLAG);

        }
    }

    /**
     * Created by houjinxin on 2016/11/8.
     */
    public static class Step3 implements IStep {
        @Override
        public StepState run(Map<String, Object> context) throws Exception {
            context.put("step3-value","value1");
            return new StepState(this.getClass().getName(), _FLOW_CONTINUE_FLAG, _STEP_SUCCESS_FLAG, "true");

        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> context = new LinkedHashMap<>();
        StepState flowState = _FLOW.run(context);
        if (flowState.getStatusFlag() == Constants._STEP_SUCCESS_FLAG) {
            logger.info("流程执行成功");
        } else if (flowState.getStatusFlag() == Constants._STEP_FAILURE_FLAG) {
            logger.info("流程执行失败");
        } else if (flowState.getStatusFlag() == Constants._STEP_SUCCESS_WITH_INFO_FLAG) {
            logger.info("流程执行成功但需要返回信息");
        }
    }
}
