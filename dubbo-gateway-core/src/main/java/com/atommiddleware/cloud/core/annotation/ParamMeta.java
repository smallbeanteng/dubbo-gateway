package com.atommiddleware.cloud.core.annotation;

import com.atommiddleware.cloud.api.annotation.ParamAttribute;

public class ParamMeta {

	public ParamMeta(String paramName, String paramType, ParamAttribute paramAttribute, boolean simpleType,
			boolean childAllSimpleType) {
		this.paramName = paramName;
		this.paramAttribute = paramAttribute;
		this.paramType = paramType;
		this.simpleType = simpleType;
		this.childAllSimpleType = childAllSimpleType;
	}

	private String paramName;

	private ParamAttribute paramAttribute;

	private String paramType;
	private boolean simpleType;

	private boolean childAllSimpleType;

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

}
