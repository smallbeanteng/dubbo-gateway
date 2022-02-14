package com.atommiddleware.cloud.security.validation;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebInputException;

import com.atommiddleware.cloud.security.utils.ValidatorUtils;

public class DefaultParamValidator implements ParamValidator {

	private final ValidatorMode validatorMode;

	public DefaultParamValidator(ValidatorMode validatorMode) {
		this.validatorMode = validatorMode;
	}

	@Override
	public void validate(Object domain) {
		if (null == domain) {
			return;
		}
		Set<ConstraintViolation<Object>> validateResult = null;
		if (validatorMode == ValidatorMode.FAST) {
			try {
				validateResult = ValidatorUtils.validateFast(domain);
			} catch (Exception e) {
				throw new ServerWebInputException("param validator fail");
			}
			if (!CollectionUtils.isEmpty(validateResult)) {
				throw new ServerWebInputException(validateResult.iterator().next().getMessage());
			}
		} else {
			try {
				validateResult = ValidatorUtils.validateAll(domain);
			} catch (Exception e) {
				throw new ServerWebInputException("param validator fail");
			}
			if (!CollectionUtils.isEmpty(validateResult)) {
				Iterator<ConstraintViolation<Object>> it = validateResult.iterator();
				StringBuilder strErrorBuilder = new StringBuilder();
				while (it.hasNext()) {
					ConstraintViolation<Object> cv = it.next();
					strErrorBuilder.append("," + cv.getMessage());
				}
				throw new ServerWebInputException(strErrorBuilder.toString());
			}
		}
	}

}
