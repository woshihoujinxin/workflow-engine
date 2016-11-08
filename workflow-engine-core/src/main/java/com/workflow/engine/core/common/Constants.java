package com.workflow.engine.core.common;

/**
 * 流程机制中用到的常量
 * Created by houjinxin on 16/3/9.
 */
public class Constants {

    /**
     * 状态码,标识流程继续
     */
    public static final int _FLOW_CONTINUE_FLAG     = 1;

    /**
     * 状态码,标识流程终止
     */
    public static final int _FLOW_FINISHED_FLAG     = 0;

    /**
     * 状态码，标识这一步结果是成功的
     */
    public static final int _STEP_SUCCESS_FLAG      = 1;

    /**
     * 状态码，标识这一步结果是成功的, 但需要返回步骤信息
     */
    public static final int _STEP_SUCCESS_WITH_INFO_FLAG = 2;

    /**
     * 状态码，标识这一步结果是失败的
     */
    public static final int _STEP_FAILURE_FLAG      = 0;

}
