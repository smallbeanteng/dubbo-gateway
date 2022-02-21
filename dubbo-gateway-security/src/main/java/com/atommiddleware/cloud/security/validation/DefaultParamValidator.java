package com.atommiddleware.cloud.security.validation;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.util.CollectionUtils;

public class DefaultParamValidator implements ParamValidator {

	@Override
	public String appendFailReason(Set<ConstraintViolation<? extends Object>> validateResult) {
		if (!CollectionUtils.isEmpty(validateResult)) {
			Iterator<ConstraintViolation<?>> it = validateResult.iterator();
			StringBuilder strErrorBuilder = new StringBuilder();
			while (it.hasNext()) {
				ConstraintViolation<?> cv = it.next();
				strErrorBuilder.append(cv.getMessage()+"," );
			}
			return strErrorBuilder.toString().substring(0,strErrorBuilder.length() - 1);
		}
		return null;
	}

}
