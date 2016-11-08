package com.workflow.engine.core.service.entity.vo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;
import java.util.Map;

/**
 * 车型信息VO
 * Created by houjinxin on 16/3/2.
 */
public class VehicleInfoVO {

    //车型查询成功标志
    private Integer code;
    //车型列表
    private List<Map<String,String>> vehicles;
    //分页信息
    private Map<String, String> pagination;
    //筛选条件
    private Map<String, String> distincts;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Map<String, String>> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Map<String, String>> vehicles) {
        this.vehicles = vehicles;
    }

    public Map<String, String> getPagination() {
        return pagination;
    }

    public void setPagination(Map<String, String> pagination) {
        this.pagination = pagination;
    }

    public Map<String, String> getDistincts() {
        return distincts;
    }

    public void setDistincts(Map<String, String> distincts) {
        this.distincts = distincts;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
