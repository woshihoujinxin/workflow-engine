package com.workflow.engine.core.service.entity.po;

import java.util.Date;

/**
 * Created by houjinxin on 16/5/10.
 */
public class QuoteRequestPO {

    private String id;
    private String autoId;//车辆信息ID
    private String applicantId;//申请人id
    private Date quoteTime;//报价时间
    private String channelId;//渠道ID
    private String insurancePackageId;//套餐ID
    private String areaId;//区域ID

    public QuoteRequestPO(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public Date getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(Date quoteTime) {
        this.quoteTime = quoteTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getInsurancePackageId() {
        return insurancePackageId;
    }

    public void setInsurancePackageId(String insurancePackageId) {
        this.insurancePackageId = insurancePackageId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
