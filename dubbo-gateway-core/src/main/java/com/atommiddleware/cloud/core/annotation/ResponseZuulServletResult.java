package com.atommiddleware.cloud.core.annotation;

import org.springframework.http.HttpStatus;

public interface ResponseZuulServletResult {
	public Object sevletZuulResponse(String result);
	
	public Object sevletZuulResponseException(HttpStatus httpStatus,String msg);
}
