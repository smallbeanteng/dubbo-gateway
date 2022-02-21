package com.atommiddleware.cloud.core.exception;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.atommiddleware.cloud.security.validation.ParamValidator;

public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {

	private final ParamValidator paramValidator;

	public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
			ErrorProperties errorProperties, ApplicationContext applicationContext, ParamValidator paramValidator) {
		super(errorAttributes, resourceProperties, errorProperties, applicationContext);
		this.paramValidator = paramValidator;
	}

	@Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
		Map<String, Object> responseExceptionMap = handleResponseException(request);
		if (!CollectionUtils.isEmpty(responseExceptionMap)) {
			return responseExceptionMap;
		}
		Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);
		return response(errorAttributes);
	}

	@Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		Map<String, Object> responseExceptionMap = handleResponseException(request);
		if (!CollectionUtils.isEmpty(responseExceptionMap)) {
			return responseExceptionMap;
		}
		Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
		return response(errorAttributes);
	}

	private Map<String, Object> handleResponseException(ServerRequest request) {
		Throwable error = super.getError(request);
		if (error instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException) error;
			String errorResult = paramValidator
					.appendFailReason(constraintViolationException.getConstraintViolations());
			Map<String, Object> errorAttributes = new HashMap<String, Object>();
			errorAttributes.put("code", HttpStatus.BAD_REQUEST.value());
			errorAttributes.put("msg",
					StringUtils.isEmpty(errorResult) ? HttpStatus.BAD_REQUEST.getReasonPhrase() : errorResult);
			return errorAttributes;
		}
		if (error instanceof ResponseStatusException) {
			ResponseStatusException responseStatusException = (ResponseStatusException) error;
			Map<String, Object> errorAttributes = new HashMap<String, Object>();
			errorAttributes.put("code", responseStatusException.getStatus().value());
			errorAttributes.put("msg",
					StringUtils.isEmpty(responseStatusException.getReason())
							? responseStatusException.getStatus().getReasonPhrase()
							: responseStatusException.getReason());
			return errorAttributes;
		} else {
			return null;
		}
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	@Override
	protected int getHttpStatus(Map<String, Object> errorAttributes) {
		int statusCode = (int) errorAttributes.get("code");
		return statusCode;
	}

	private Map<String, Object> response(Map<String, Object> errorAttributes) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", errorAttributes.get("status"));
		map.put("msg", errorAttributes.get("error"));
		return map;
	}
}
