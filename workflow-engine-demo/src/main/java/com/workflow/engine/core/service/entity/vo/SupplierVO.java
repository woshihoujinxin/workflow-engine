package com.workflow.engine.core.service.entity.vo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 保险公司
 * Created by houjinxin on 16/3/16.
 */
public class SupplierVO {

    private String id;
    private String supplierName; //保险公司名称
    private String supplierCode; //保险公司代码

    public SupplierVO(String supplierName, String supplierCode) {
        this.supplierName = supplierName;
        this.supplierCode = supplierCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
