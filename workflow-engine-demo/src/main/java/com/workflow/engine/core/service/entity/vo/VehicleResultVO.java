package com.workflow.engine.core.service.entity.vo;

import java.io.Serializable;

public class VehicleResultVO implements Serializable{

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8667192320643118703L;
	
	private boolean success;
	private String code;
	
	private Data data;
	public static class Data implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2151698039090758042L;
		private String vehicle_id;
		private String model_code;
		public String getModel_code() {
			return model_code;
		}
		public void setModel_code(String model_code) {
			this.model_code = model_code;
		}
		public String getVehicle_id() {
			return vehicle_id;
		}
		public void setVehicle_id(String vehicle_id) {
			this.vehicle_id = vehicle_id;
		}
	}
}
