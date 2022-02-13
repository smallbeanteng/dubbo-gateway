package com.atommiddleware.cloud.core.security;

public interface XssSecurity {
	
	String xssClean(String origionText);
	 
	public enum XssFilterStrategy{
		RESPONSE,REQUEST
	}
	
	public enum XssFilterType{
		ANTISAMY,ESAPI,ENCODER_ESAPI
	}
}
