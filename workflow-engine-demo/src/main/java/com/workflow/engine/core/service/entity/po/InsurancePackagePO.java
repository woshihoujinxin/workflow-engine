package com.workflow.engine.core.service.entity.po;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 套餐
 * Created by houjinxin on 16/3/24.
 */
public class InsurancePackagePO implements Serializable {

    private String id;
    //交强险
    private boolean compulsory;
    //车船税
    private boolean autoTax;
    //机动车损险
    private boolean damage;
    //机动车损险不计免赔
    private boolean exemptDamage;
    //第三方责任险
    private double thirdParty;
    //第三方责任险不计免赔
    private boolean exemptThirdParty;
    //司机险
    private double driver;
    //司机险不计免赔
    private boolean exemptDriver;
    //乘客险
    private double passenger;
    //乘客险不计免赔
    private boolean exemptPassenger;
    //盗抢险
    private boolean theft;
    //盗抢险不计免赔
    private boolean exemptTheft;
    //玻璃险
    private String glass;
    //划痕险
    private double scratch;
    //划痕险不计免赔
    private boolean exemptScratch;
    //自燃险
    private boolean spontaneousCombustion;
    //自燃险不计免赔
    private boolean exemptSpontaneousCombustion;
    //涉水险
    private boolean engine;
    //涉水险不计免赔
    private boolean exemptEngine;
    //倒车镜
    private boolean rearView;
    //指定专修厂 与玻璃类似分国产和进口
    private String specialFactory;
    //一般保险公司都有四中类型的套餐基本,大众,全面,自定义
    private String type;

    public InsurancePackagePO(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public boolean isAutoTax() {
        return autoTax;
    }

    public void setAutoTax(boolean autoTax) {
        this.autoTax = autoTax;
    }

    public boolean isDamage() {
        return damage;
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
    }

    public boolean isExemptDamage() {
        return exemptDamage;
    }

    public void setExemptDamage(boolean exemptDamage) {
        this.exemptDamage = exemptDamage;
    }

    public double getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(double thirdParty) {
        this.thirdParty = thirdParty;
    }

    public boolean isExemptThirdParty() {
        return exemptThirdParty;
    }

    public void setExemptThirdParty(boolean exemptThirdParty) {
        this.exemptThirdParty = exemptThirdParty;
    }

    public double getDriver() {
        return driver;
    }

    public void setDriver(double driver) {
        this.driver = driver;
    }

    public boolean isExemptDriver() {
        return exemptDriver;
    }

    public void setExemptDriver(boolean exemptDriver) {
        this.exemptDriver = exemptDriver;
    }

    public double getPassenger() {
        return passenger;
    }

    public void setPassenger(double passenger) {
        this.passenger = passenger;
    }

    public boolean isExemptPassenger() {
        return exemptPassenger;
    }

    public void setExemptPassenger(boolean exemptPassenger) {
        this.exemptPassenger = exemptPassenger;
    }

    public boolean isTheft() {
        return theft;
    }

    public void setTheft(boolean theft) {
        this.theft = theft;
    }

    public boolean isExemptTheft() {
        return exemptTheft;
    }

    public void setExemptTheft(boolean exemptTheft) {
        this.exemptTheft = exemptTheft;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public double getScratch() {
        return scratch;
    }

    public void setScratch(double scratch) {
        this.scratch = scratch;
    }

    public boolean isExemptScratch() {
        return exemptScratch;
    }

    public void setExemptScratch(boolean exemptScratch) {
        this.exemptScratch = exemptScratch;
    }

    public boolean isSpontaneousCombustion() {
        return spontaneousCombustion;
    }

    public void setSpontaneousCombustion(boolean spontaneousCombustion) {
        this.spontaneousCombustion = spontaneousCombustion;
    }

    public boolean isExemptSpontaneousCombustion() {
        return exemptSpontaneousCombustion;
    }

    public void setExemptSpontaneousCombustion(boolean exemptSpontaneousCombustion) {
        this.exemptSpontaneousCombustion = exemptSpontaneousCombustion;
    }

    public boolean isEngine() {
        return engine;
    }

    public void setEngine(boolean engine) {
        this.engine = engine;
    }

    public boolean isExemptEngine() {
        return exemptEngine;
    }

    public void setExemptEngine(boolean exemptEngine) {
        this.exemptEngine = exemptEngine;
    }

    public boolean isRearView() {
        return rearView;
    }

    public void setRearView(boolean rearView) {
        this.rearView = rearView;
    }

    public String getSpecialFactory() {
        return specialFactory;
    }

    public void setSpecialFactory(String specialFactory) {
        this.specialFactory = specialFactory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
