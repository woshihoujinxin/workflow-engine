package com.workflow.engine.core.common;


import com.workflow.engine.core.common.flow.IChecker;
import com.workflow.engine.core.common.flow.IRouter;
import com.workflow.engine.core.common.flow.IStep;
import com.workflow.engine.core.common.flow.impl.ConditionRouter;
import com.workflow.engine.core.common.flow.impl.Flow;
import com.workflow.engine.core.common.flow.impl.SimpleRouter;
import com.workflow.engine.core.common.flow.impl.StringChecker;

import java.util.*;

/**
 * 流程构建器
 * Created by houjinxin on 16/3/9.
 */
public class FlowBuilder {

    private Map<String, Class<? extends IStep>> nameClazzMapping;
    private LinkedList<Object> steps;

    public FlowBuilder(Map<String, Class<? extends IStep>> nameClazzMapping, LinkedList<Object> steps) {
        this.nameClazzMapping = nameClazzMapping;
        this.steps = steps;
    }

    public Flow build() {
        //流程构建的最终结果是第一步及其Router故起名为firstStepWithRouter
        Tuple2<Object, IRouter> firstStepWithRouter = null;
        try {
            //递归创建由当前step和决定下一步的router组成的二元元组
            firstStepWithRouter = doBuild((LinkedList<Object>) steps.clone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Flow(firstStepWithRouter);
    }

    private Tuple2<Object, IRouter> doBuild(LinkedList<Object> steps) throws IllegalAccessException, InstantiationException {
        //首元素出队列
        Object stepDescriptor = steps.removeFirst();
        //step可能是IStep的实现或者分支流程映射
        Object step;
        if (stepDescriptor instanceof String) {
            if(nameClazzMapping.get(stepDescriptor) != null){
                step = nameClazzMapping.get(stepDescriptor).newInstance();
            } else {
                throw new NullPointerException("[ " + stepDescriptor + " ]不在nameClazzMapping中, 请确定该步骤已经注册!");
            }
        } else {
            step = buildForkFlows((Map<String, List<Object>>) stepDescriptor);
        }

        //构造router
        IRouter router;
        if (steps.size() != 0) { //有后续步骤
            Object nextStepDescriptor = steps.getFirst();
            //递归构造后续流程
            Tuple2<Object, IRouter> nextStepWithRouter = doBuild(steps);
            if (nextStepDescriptor instanceof String) {
                router = new SimpleRouter(nextStepWithRouter);
            } else {
                router = new ConditionRouter(nextStepWithRouter);
            }
        } else { //递归出口
            router = new SimpleRouter(null);
        }
        return new Tuple2<>(step, router);
    }

    /**
     * 构建分支流程，返回分支标识与流程构成的映射
     *
     * @param stepDescriptor 包含流程分支信息的Map, Map的key用于标识分支,value代表一个子分支,子分支一定是list类型的
     * @return 返回分支标识与流程构成的映射
     */
    private Object buildForkFlows(Map<String, List<Object>> stepDescriptor) {
        //用于记录checker和forkFlow的映射关系
        Map<IChecker<String>, Flow> checkerForkFlows = new HashMap<>();
        //构造checker和flow的映射关系
        Set<Map.Entry<String, List<Object>>> entrySet= stepDescriptor.entrySet();
        for (Map.Entry<String, List<Object>> entry: entrySet) {
            //用来匹配StepState中的forkFlag和stepDescriptor的key,以决定流程运转中选择哪个分支
            IChecker<String> checker = new StringChecker(entry.getKey());
            //构建分支流程
            Flow forkFlow = new FlowBuilder(nameClazzMapping, new LinkedList<>(entry.getValue())).build();
            checkerForkFlows.put(checker, forkFlow);
        }
        return checkerForkFlows;
    }

}
