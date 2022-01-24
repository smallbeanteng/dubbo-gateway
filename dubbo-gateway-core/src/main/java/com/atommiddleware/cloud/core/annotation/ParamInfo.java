package com.atommiddleware.cloud.core.annotation;

public class ParamInfo {
	
	public ParamInfo(int index,String paramName,int paramFromType,String paramType,boolean required) {
		this.index=index;
		this.paramName=paramName;
		this.paramFromType=paramFromType;
		this.paramType=paramType;
		this.required=required;
	}

	private int index;
	
	private String paramName;
	
	private int paramFromType;
	
	private String paramType;
	
	private boolean required;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public int getParamFromType() {
		return paramFromType;
	}

	public void setParamFromType(int paramFromType) {
		this.paramFromType = paramFromType;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
