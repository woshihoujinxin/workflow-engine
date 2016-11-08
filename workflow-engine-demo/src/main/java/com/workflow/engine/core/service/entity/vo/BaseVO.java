package com.workflow.engine.core.service.entity.vo;

import java.io.Serializable;

public class BaseVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7697891143571565098L;
	private String code="0";
	private String message="查询成功";

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
