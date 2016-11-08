package com.workflow.engine.core.common;

/**
 * 步骤状态描述
 * Created by houjinxin on 16/3/9.
 */
public class StepState {

    private String stepName;
    //步骤执行是否成功 1--成功 0--失败 以及其他
    private int statusFlag;
    //流程是否继续标志 1--继续 0--终止
    private int continueFlag;
    //分支标志
    private Object forkFlag;
    //步骤返回信息
    private Object stepInfo;
    //错误消息枚举 提供一个默认的枚举值,防止取值是出现空指针
    private MsgEnum msgEnum = MsgEnum.Other;

    /**
     * 用于没有后续分支的步骤
     * @param stepName 步骤名称
     * @param statusFlag 步骤执行是否成功
     * @param continueFlag 流程是否继续标志
     */
    public StepState(String stepName, int statusFlag, int continueFlag) {
        this.stepName = stepName;
        this.statusFlag = statusFlag;
        this.continueFlag = continueFlag;
    }

    /**
     * 用于有后续分支的步骤
     * @param stepName 步骤名称
     * @param statusFlag 步骤执行是否成功
     * @param continueFlag 流程是否继续标志
     * @param forkFlag 分支标志
     */
    public StepState(String stepName, int statusFlag, int continueFlag, Object forkFlag) {
        this.stepName = stepName;
        this.statusFlag = statusFlag;
        this.continueFlag = continueFlag;
        this.forkFlag = forkFlag;
    }

    /**
     * 用于需要返回某一步骤特定信息的情况
     * @param stepName 步骤名称
     * @param statusFlag 步骤执行是否成功
     * @param continueFlag 流程是否继续标志
     * @param forkFlag 分支标志
     * @param stepInfo 步骤返回信息
     */
    public StepState(String stepName, int statusFlag, int continueFlag, Object forkFlag, Object stepInfo, MsgEnum msgEnum) {
        this.stepName = stepName;
        this.statusFlag = statusFlag;
        this.continueFlag = continueFlag;
        this.forkFlag = forkFlag;
        this.stepInfo = stepInfo;
        this.msgEnum = msgEnum;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public int getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(int statusFlag) {
        this.statusFlag = statusFlag;
    }

    public int getContinueFlag() {
        return continueFlag;
    }

    public void setContinueFlag(int continueFlag) {
        this.continueFlag = continueFlag;
    }

    public Object getForkFlag() {
        return forkFlag;
    }

    public void setForkFlag(Object forkFlag) {
        this.forkFlag = forkFlag;
    }

    public Object getStepInfo() {
        return stepInfo;
    }

    public void setStepInfo(Object stepInfo) {
        this.stepInfo = stepInfo;
    }

	public MsgEnum getMsgEnum() {
		return msgEnum;
	}

	public void setMsgEnum(MsgEnum msgEnum) {
		this.msgEnum = msgEnum;
	}
    
    
}
