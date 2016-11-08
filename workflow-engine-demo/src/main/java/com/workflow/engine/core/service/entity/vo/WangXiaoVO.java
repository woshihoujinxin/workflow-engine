package com.workflow.engine.core.service.entity.vo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * 为了兼容旧代码采用继承的方式
 * Created by houjinxin on 16/3/29.
 */
public class WangXiaoVO {
    private Long id;
    private ApplicantVO applicant;
    private AutoVO auto;
    private List<SupplierVO> suppliers;
    private InsurancePackageVO insurancePackage;
    private String cityCode;//报价城市ID
    private String channelName;
    //查询车型选中车辆标识
    private String markID;
    
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicantVO getApplicant() {
        return applicant;
    }

    public void setApplicant(ApplicantVO applicant) {
        this.applicant = applicant;
    }

    public AutoVO getAuto() {
        return auto;
    }

    public void setAuto(AutoVO auto) {
        this.auto = auto;
    }

    public List<SupplierVO> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<SupplierVO> suppliers) {
        this.suppliers = suppliers;
    }

    public InsurancePackageVO getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackageVO insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public String getMarkID() {
        return markID;
    }

    public void setMarkID(String markID) {
        this.markID = markID;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
