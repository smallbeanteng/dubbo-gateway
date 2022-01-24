package com.atommiddleware.cloud.core.annotation;

import com.atommiddleware.cloud.api.annotation.ParamAttribute;

public class ParamMeta {
	
	public ParamMeta(String paramName,String paramType,ParamAttribute paramAttribute) {
		this.paramName=paramName;
		this.paramAttribute=paramAttribute;
		this.paramType=paramType;
	}

	private String paramName;
	
	private ParamAttribute paramAttribute;
	
	private String paramType;

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public ParamAttribute getParamAttribute() {
		return paramAttribute;
	}

	public void setParamAttribute(ParamAttribute paramAttribute) {
		this.paramAttribute = paramAttribute;
	}
	
	
}
