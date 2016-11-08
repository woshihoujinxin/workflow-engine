package com.workflow.engine.core.service.entity.po;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

import static com.workflow.engine.core.common.utils.DoubleUtils.displayDoubleValue;
import static com.workflow.engine.core.common.utils.DoubleUtils.doubleValue;

public class QuoteRecordPO {

    private String id;
    private String quoteRequestId;//报价请求ID
    private String supplierId;//保险公司

    private Date effectiveDate;//生效日期
    private Date compulsoryEffectiveDate;//生效日期
    private Date expireDate;//失效日期
    private Date compulsoryExpireDate;//失效日期

    private Double compulsoryPremium = 0.0;//交通强制险
    private Double autoTax = 0.0;//车船使用税

    private Double damagePremium = 0.0;//车损险保费
    private Double damageAmount = 0.0;// 车损险保额
    private Double exemptDamage = 0.0;//车损不计免赔

    private Double thirdPartyPremium = 0.0;//三者险保费
    private Double thirdPartyAmount = 0.0;//三者险保额
    private Double exemptThirdParty = 0.0;//三者不计免赔

    private Double driverPremium = 0.0;//车上人员（司机）保费
    private Double driverAmount = 0.0;//车上人员（司机）保额
    private Double exemptDriver = 0.0;//车上人员（司机）不计免赔

    private Double passengerPremium = 0.0;//车上人员（乘客）保费
    private Double passengerAmount = 0.0;//车上人员（乘客）保额
    private Double exemptPassenger = 0.0;//车上人员（乘客）不计免赔

    private Integer passengerCount = 0;//车上人员（乘客）数量

    private Double theftPremium = 0.0;//盗抢险保费
    private Double theftAmount = 0.0;//盗抢险保额
    private Double exemptTheft = 0.0;//盗抢不计免赔

    private Double glassPremium = 0.0;//玻璃单独破碎险保费
    private Double glassAmount = 0.0;//玻璃单独破碎险保额

    private Double scratchPremium = 0.0; //划痕险保费
    private Double scratchAmount = 0.0; //划痕险保额
    private Double exemptScratch = 0.0; //划痕险不计免赔保费

    private Double spontaneousCombustionPremium = 0.0;//自燃损失险保费
    private Double SpontaneousCombustionAmount = 0.0;//自燃损失险保额
    private Double exemptSpontaneousCombustion = 0.0;//自燃损失险保额

    private Double enginePremium = 0.0;//发动机特别险保费
    private Double engineAmount = 0.0;//发动机特别险保费
    private Double exemptEngine = 0.0;//发动机特别险不计免赔

    private Double exemptTotal = 0.0;//不计免赔总额

    private Double rearViewPremium = 0.0;//倒车镜保费
    private Double specialFactoryPremium = 0.0;//指定专修厂保费

    private Double totalPremium = 0.0;//商业险总保费
    private Date createTime;
    private Date updateTime;

    public QuoteRecordPO(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuoteRequestId() {
        return quoteRequestId;
    }

    public void setQuoteRequestId(String quoteRequestId) {
        this.quoteRequestId = quoteRequestId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getCompulsoryEffectiveDate() {
        return compulsoryEffectiveDate;
    }

    public void setCompulsoryEffectiveDate(Date compulsoryEffectiveDate) {
        this.compulsoryEffectiveDate = compulsoryEffectiveDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getCompulsoryExpireDate() {
        return compulsoryExpireDate;
    }

    public void setCompulsoryExpireDate(Date compulsoryExpireDate) {
        this.compulsoryExpireDate = compulsoryExpireDate;
    }

    public Double getCompulsoryPremium() {
        return compulsoryPremium;
    }

    public void setCompulsoryPremium(Double compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium;
    }

    public Double getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(Double autoTax) {
        this.autoTax = autoTax;
    }

    public Double getDamagePremium() {
        return damagePremium;
    }

    public void setDamagePremium(Double damagePremium) {
        this.damagePremium = damagePremium;
    }

    public Double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(Double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public Double getExemptDamage() {
        return exemptDamage;
    }

    public void setExemptDamage(Double exemptDamage) {
        this.exemptDamage = exemptDamage;
    }

    public Double getThirdPartyPremium() {
        return thirdPartyPremium;
    }

    public void setThirdPartyPremium(Double thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium;
    }

    public Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    public Double getExemptThirdParty() {
        return exemptThirdParty;
    }

    public void setExemptThirdParty(Double exemptThirdParty) {
        this.exemptThirdParty = exemptThirdParty;
    }

    public Double getDriverPremium() {
        return driverPremium;
    }

    public void setDriverPremium(Double driverPremium) {
        this.driverPremium = driverPremium;
    }

    public Double getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    public Double getExemptDriver() {
        return exemptDriver;
    }

    public void setExemptDriver(Double exemptDriver) {
        this.exemptDriver = exemptDriver;
    }

    public Double getPassengerPremium() {
        return passengerPremium;
    }

    public void setPassengerPremium(Double passengerPremium) {
        this.passengerPremium = passengerPremium;
    }

    public Double getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    public Double getExemptPassenger() {
        return exemptPassenger;
    }

    public void setExemptPassenger(Double exemptPassenger) {
        this.exemptPassenger = exemptPassenger;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public Double getTheftPremium() {
        return theftPremium;
    }

    public void setTheftPremium(Double theftPremium) {
        this.theftPremium = theftPremium;
    }

    public Double getTheftAmount() {
        return theftAmount;
    }

    public void setTheftAmount(Double theftAmount) {
        this.theftAmount = theftAmount;
    }

    public Double getExemptTheft() {
        return exemptTheft;
    }

    public void setExemptTheft(Double exemptTheft) {
        this.exemptTheft = exemptTheft;
    }

    public Double getGlassPremium() {
        return glassPremium;
    }

    public void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium;
    }

    public Double getGlassAmount() {
        return glassAmount;
    }

    public void setGlassAmount(Double glassAmount) {
        this.glassAmount = glassAmount;
    }

    public Double getScratchPremium() {
        return scratchPremium;
    }

    public void setScratchPremium(Double scratchPremium) {
        this.scratchPremium = scratchPremium;
    }

    public Double getScratchAmount() {
        return scratchAmount;
    }

    public void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
    }

    public Double getExemptScratch() {
        return exemptScratch;
    }

    public void setExemptScratch(Double exemptScratch) {
        this.exemptScratch = exemptScratch;
    }

    public Double getSpontaneousCombustionPremium() {
        return spontaneousCombustionPremium;
    }

    public void setSpontaneousCombustionPremium(Double spontaneousCombustionPremium) {
        this.spontaneousCombustionPremium = spontaneousCombustionPremium;
    }

    public Double getSpontaneousCombustionAmount() {
        return SpontaneousCombustionAmount;
    }

    public void setSpontaneousCombustionAmount(Double spontaneousCombustionAmount) {
        this.SpontaneousCombustionAmount = spontaneousCombustionAmount;
    }

    public Double getExemptSpontaneousCombustion() {
        return exemptSpontaneousCombustion;
    }

    public void setExemptSpontaneousCombustion(Double exemptSpontaneousCombustion) {
        this.exemptSpontaneousCombustion = exemptSpontaneousCombustion;
    }

    public Double getEnginePremium() {
        return enginePremium;
    }

    public void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium;
    }

    public Double getEngineAmount() {
        return engineAmount;
    }

    public void setEngineAmount(Double engineAmount) {
        this.engineAmount = engineAmount;
    }

    public Double getExemptEngine() {
        return exemptEngine;
    }

    public void setExemptEngine(Double exemptEngine) {
        this.exemptEngine = exemptEngine;
    }

    public Double getExemptTotal() {
        return exemptTotal;
    }

    public void setExemptTotal(Double exemptTotal) {
        this.exemptTotal = exemptTotal;
    }

    public Double getRearViewPremium() {
        return rearViewPremium;
    }

    public void setRearViewPremium(Double rearViewPremium) {
        this.rearViewPremium = rearViewPremium;
    }

    public Double getSpecialFactoryPremium() {
        return specialFactoryPremium;
    }

    public void setSpecialFactoryPremium(Double specialFactoryPremium) {
        this.specialFactoryPremium = specialFactoryPremium;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Double getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(Double totalPremium) {
        this.totalPremium = totalPremium;
    }

    /**
     * 不计免赔分项求和
     * @return
     */
    public Double sumExemptItems() {
        return displayDoubleValue(doubleValue(this.getExemptDamage())
                        + doubleValue(this.getExemptThirdParty())
                        + doubleValue(this.getExemptDriver())
                        + doubleValue(this.getExemptPassenger())
                        + doubleValue(this.getExemptTheft())
                        + doubleValue(this.getExemptScratch())
                        + doubleValue(this.getExemptSpontaneousCombustion())
                        + doubleValue(this.getExemptEngine())
        );
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
