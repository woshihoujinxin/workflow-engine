package test.steps;

import com.workflow.engine.core.common.Constants;
import com.workflow.engine.core.common.FlowBuilder;
import com.workflow.engine.core.common.StepState;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.flow.impl.Flow;
import com.workflow.engine.core.common.utils.FlowBuilderUtil;
import org.junit.Before;
import org.junit.Test;
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
public class TestFlowExecute {

    private static Logger logger = LoggerFactory.getLogger(TestFlowExecute.class);

    private Flow _FLOW;

    @Before
    public void setUp(){
        String flowFilePath = "src/test/resources/test.flows";
        Map<String, Class<? extends IStep>> nameStepClazzMappings = new HashMap<String, Class<? extends IStep>>() {{
            put("step1", Step1.class);
            put("step2", Step2.class);
            put("step3", Step3.class);
        }};
        LinkedList<Object> flowStepOrderConfig = FlowBuilderUtil.extractSteps(flowFilePath);
        _FLOW = new FlowBuilder(nameStepClazzMappings, flowStepOrderConfig).build();
    }

    @Test
    public void testFlowExecute() throws Exception {
        StepState flowState = _FLOW.run(new LinkedHashMap<>());
        if (flowState.getStatusFlag() == Constants._STEP_SUCCESS_FLAG) {
            logger.info("流程执行成功");
        } else if (flowState.getStatusFlag() == Constants._STEP_FAILURE_FLAG) {
            logger.info("流程执行失败");
        } else if (flowState.getStatusFlag() == Constants._STEP_SUCCESS_WITH_INFO_FLAG) {
            logger.info("流程执行成功但需要返回信息");
        }
    }

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

}
