package com.workflow.engine.core.service.entity.vo;

/**
 * 车型查询条件,包括以下几个:
 * 1.必须存在,但值可以为空的有: k, page, market_date
 * 2.可有可无的有: brand_name, family_name, engine_desc, gearbox_name, vehicle_fgw_code
 * Created by houjinxin on 16/3/2.
 */
public class VehicleCriteriaVO {

    private String k;
    private String page;
    private String market_date;
    private String brand_name;
    private String family_name;
    private String engine_desc;
    private String gearbox_name;
    private String vehicle_fgw_code;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getMarket_date() {
        return market_date;
    }

    public void setMarket_date(String market_date) {
        this.market_date = market_date;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getEngine_desc() {
        return engine_desc;
    }

    public void setEngine_desc(String engine_desc) {
        this.engine_desc = engine_desc;
    }

    public String getGearbox_name() {
        return gearbox_name;
    }

    public void setGearbox_name(String gearbox_name) {
        this.gearbox_name = gearbox_name;
    }

    public String getVehicle_fgw_code() {
        return vehicle_fgw_code;
    }

    public void setVehicle_fgw_code(String vehicle_fgw_code) {
        this.vehicle_fgw_code = vehicle_fgw_code;
    }
}
