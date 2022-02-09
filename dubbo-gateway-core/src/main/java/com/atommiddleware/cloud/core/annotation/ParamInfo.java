package com.atommiddleware.cloud.core.annotation;

public class ParamInfo {
	
	public ParamInfo(int index,String paramName,int paramFromType,String paramType,int paramFormat, boolean simpleType,boolean childAllSimpleType,boolean required) {
		this.index=index;
		this.paramName=paramName;
		this.paramFromType=paramFromType;
		this.paramType=paramType;
		this.simpleType=simpleType;
		this.childAllSimpleType=simpleType;
		this.required=required;
		this.paramFormat=paramFormat;
	}

	private int index;
	
	private String paramName;
	
	private int paramFromType;
	
	private String paramType;
	
	private boolean simpleType;
	
	private boolean childAllSimpleType;
	
	private boolean required;
	
	private int paramFormat;
	
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

	public boolean isSimpleType() {
		return simpleType;
	}

	public void setSimpleType(boolean simpleType) {
		this.simpleType = simpleType;
	}

	public boolean isChildAllSimpleType() {
		return childAllSimpleType;
	}

	public void setChildAllSimpleType(boolean childAllSimpleType) {
		this.childAllSimpleType = childAllSimpleType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getParamFormat() {
		return paramFormat;
	}

	public void setParamFormat(int paramFormat) {
		this.paramFormat = paramFormat;
	}
}
