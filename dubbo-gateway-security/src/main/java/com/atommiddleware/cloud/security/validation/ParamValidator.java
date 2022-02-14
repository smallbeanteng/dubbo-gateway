package com.atommiddleware.cloud.security.validation;

public interface ParamValidator {

	void validate(Object domain);
	
	public enum ValidatorMode{
		FAST,ALL
	}
}
