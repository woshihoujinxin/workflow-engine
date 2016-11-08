package com.workflow.engine.core.service.entity.po;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class VehiclePO implements Serializable{

	private static final long serialVersionUID = -8860693284417601621L;

	String plateNo;
	@JSONField(name="carVIN")
	String vin;
	String engineNo;
	String registerDate;
	String licenseOwner;
	String abs;
	String vehicleModelShort;
	String vehicleAlias;
	@JSONField(name="EmptyWeight")
	String emptyWeight;
	String seatCount;
	String groupCode;
	String seatMin;
	String batholith;
	String yearPattern;
	String brandCode;
	String kindPrice;
	String gearBoxType;
	String vehicleFgwCode;
	String industryVehicleName;
	String marketYear;
	String engineCapacity;
	String groupName;
	String dumpTrailerFlag;
	String factoryName;
	String vehicleFgwName;
	String vehiclePower;
	String vehicleClassCode;
	String vehicleBrand;
	String moldCharacterCode;
	String antiTheft;
	String insuranceClass;
	String moldName;
	String riskflagCode;
	String price;
	String sum;
	String vehicleClassName;
	String purchasePrice;
	String makerModel;
	String vehicleModel;
	String taxPrice;
	@JSONField(name="rVehicleFamily")
	String vehicleFamily;
	String insuranceCode;
	String fullWeightMin;
	String familyCode;
	String engineDesc;
	String industryVehicleCode;
	String bodyType;
	String fullWeightMax;
	boolean isNew = true;

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getLicenseOwner() {
		return licenseOwner;
	}

	public void setLicenseOwner(String licenseOwner) {
		this.licenseOwner = licenseOwner;
	}

	public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}

	public String getVehicleModelShort() {
		return vehicleModelShort;
	}

	public void setVehicleModelShort(String vehicleModelShort) {
		this.vehicleModelShort = vehicleModelShort;
	}

	public String getVehicleAlias() {
		return vehicleAlias;
	}

	public void setVehicleAlias(String vehicleAlias) {
		this.vehicleAlias = vehicleAlias;
	}

	public String getEmptyWeight() {
		return emptyWeight;
	}

	public void setEmptyWeight(String emptyWeight) {
		this.emptyWeight = emptyWeight;
	}

	public String getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(String seatCount) {
		this.seatCount = seatCount;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getSeatMin() {
		return seatMin;
	}

	public void setSeatMin(String seatMin) {
		this.seatMin = seatMin;
	}

	public String getBatholith() {
		return batholith;
	}

	public void setBatholith(String batholith) {
		this.batholith = batholith;
	}

	public String getYearPattern() {
		return yearPattern;
	}

	public void setYearPattern(String yearPattern) {
		this.yearPattern = yearPattern;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getKindPrice() {
		return kindPrice;
	}

	public void setKindPrice(String kindPrice) {
		this.kindPrice = kindPrice;
	}

	public String getGearBoxType() {
		return gearBoxType;
	}

	public void setGearBoxType(String gearBoxType) {
		this.gearBoxType = gearBoxType;
	}

	public String getVehicleFgwCode() {
		return vehicleFgwCode;
	}

	public void setVehicleFgwCode(String vehicleFgwCode) {
		this.vehicleFgwCode = vehicleFgwCode;
	}

	public String getIndustryVehicleName() {
		return industryVehicleName;
	}

	public void setIndustryVehicleName(String industryVehicleName) {
		this.industryVehicleName = industryVehicleName;
	}

	public String getMarketYear() {
		return marketYear;
	}

	public void setMarketYear(String marketYear) {
		this.marketYear = marketYear;
	}

	public String getEngineCapacity() {
		return engineCapacity;
	}

	public void setEngineCapacity(String engineCapacity) {
		this.engineCapacity = engineCapacity;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDumpTrailerFlag() {
		return dumpTrailerFlag;
	}

	public void setDumpTrailerFlag(String dumpTrailerFlag) {
		this.dumpTrailerFlag = dumpTrailerFlag;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public String getVehicleFgwName() {
		return vehicleFgwName;
	}

	public void setVehicleFgwName(String vehicleFgwName) {
		this.vehicleFgwName = vehicleFgwName;
	}

	public String getVehiclePower() {
		return vehiclePower;
	}

	public void setVehiclePower(String vehiclePower) {
		this.vehiclePower = vehiclePower;
	}

	public String getVehicleClassCode() {
		return vehicleClassCode;
	}

	public void setVehicleClassCode(String vehicleClassCode) {
		this.vehicleClassCode = vehicleClassCode;
	}

	public String getVehicleBrand() {
		return vehicleBrand;
	}

	public void setVehicleBrand(String vehicleBrand) {
		this.vehicleBrand = vehicleBrand;
	}

	public String getMoldCharacterCode() {
		return moldCharacterCode;
	}

	public void setMoldCharacterCode(String moldCharacterCode) {
		this.moldCharacterCode = moldCharacterCode;
	}

	public String getAntiTheft() {
		return antiTheft;
	}

	public void setAntiTheft(String antiTheft) {
		this.antiTheft = antiTheft;
	}

	public String getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClass(String insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	public String getMoldName() {
		return moldName;
	}

	public void setMoldName(String moldName) {
		this.moldName = moldName;
	}

	public String getRiskflagCode() {
		return riskflagCode;
	}

	public void setRiskflagCode(String riskflagCode) {
		this.riskflagCode = riskflagCode;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getVehicleClassName() {
		return vehicleClassName;
	}

	public void setVehicleClassName(String vehicleClassName) {
		this.vehicleClassName = vehicleClassName;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getMakerModel() {
		return makerModel;
	}

	public void setMakerModel(String makerModel) {
		this.makerModel = makerModel;
	}

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(String taxPrice) {
		this.taxPrice = taxPrice;
	}

	public String getVehicleFamily() {
		return vehicleFamily;
	}

	public void setVehicleFamily(String vehicleFamily) {
		this.vehicleFamily = vehicleFamily;
	}

	public String getInsuranceCode() {
		return insuranceCode;
	}

	public void setInsuranceCode(String insuranceCode) {
		this.insuranceCode = insuranceCode;
	}

	public String getFullWeightMin() {
		return fullWeightMin;
	}

	public void setFullWeightMin(String fullWeightMin) {
		this.fullWeightMin = fullWeightMin;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getEngineDesc() {
		return engineDesc;
	}

	public void setEngineDesc(String engineDesc) {
		this.engineDesc = engineDesc;
	}

	public String getIndustryVehicleCode() {
		return industryVehicleCode;
	}

	public void setIndustryVehicleCode(String industryVehicleCode) {
		this.industryVehicleCode = industryVehicleCode;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getFullWeightMax() {
		return fullWeightMax;
	}

	public void setFullWeightMax(String fullWeightMax) {
		this.fullWeightMax = fullWeightMax;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}
}
