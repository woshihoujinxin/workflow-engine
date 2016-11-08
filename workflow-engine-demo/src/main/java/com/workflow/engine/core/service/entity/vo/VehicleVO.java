package com.workflow.engine.core.service.entity.vo;

import java.io.Serializable;

public class VehicleVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1277939464891457163L;
	private String license_no;
	private String license_owner;
	private String city_code;
	private String phone_no;
	private String pro_code;
	private String frame_no;
	private String engine_no;
	public String getFrame_no() {
		return frame_no;
	}
	public void setFrame_no(String frame_no) {
		this.frame_no = frame_no;
	}
	
	public String getEngine_no() {
		return engine_no;
	}
	public void setEngine_no(String engine_no) {
		this.engine_no = engine_no;
	}
	public String getPro_code() {
		return pro_code;
	}
	public void setPro_code(String pro_code) {
		this.pro_code = pro_code;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getLicense_no() {
		return license_no;
	}
	public void setLicense_no(String license_no) {
		this.license_no = license_no;
	}
	public String getLicense_owner() {
		return license_owner;
	}
	public void setLicense_owner(String license_owner) {
		this.license_owner = license_owner;
	}
	public String getCity_code() {
		return city_code;
	}
	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}
}
