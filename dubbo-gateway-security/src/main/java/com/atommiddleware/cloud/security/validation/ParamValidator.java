package com.atommiddleware.cloud.security.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

public interface ParamValidator {
	
	String appendFailReason(Set<ConstraintViolation<?>> validateResult);
	
}
