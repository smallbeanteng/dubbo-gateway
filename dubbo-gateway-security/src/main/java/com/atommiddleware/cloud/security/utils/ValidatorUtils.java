package com.atommiddleware.cloud.security.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;

public class ValidatorUtils {

	private static Validator validatorFast = Validation.byProvider(HibernateValidator.class).configure().failFast(true)
			.buildValidatorFactory().getValidator();
	private static Validator validatorAll = Validation.byProvider(HibernateValidator.class).configure().failFast(false)
			.buildValidatorFactory().getValidator();

	public static <T> Set<ConstraintViolation<T>> validateFast(T domain) throws Exception {
		Set<ConstraintViolation<T>> validateResult = validatorFast.validate(domain);
		return validateResult;
	}

	public static <T> Set<ConstraintViolation<T>> validateAll(T domain) throws Exception {
		Set<ConstraintViolation<T>> validateResult = validatorAll.validate(domain);
		return validateResult;
	}

}
