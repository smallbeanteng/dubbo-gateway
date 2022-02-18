package com.atommiddleware.cloud.core.security;

public interface XssSecurity {

	String xssClean(String origionText);

	public enum XssFilterStrategy {
		RESPONSE, REQUEST
	}

	public enum XssFilterMode {
		ESAPI, ANTISAMY, ESAPI_EASY;
		public String valueString() {
			return String.valueOf(this.ordinal());
		}
	}
}
