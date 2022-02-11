package com.atommiddleware.cloud.core.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

public interface ResponseServletResult {
	
	public void sevletResponse(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,String result);
	
	public void sevletResponseException(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,HttpStatus httpStatus,String msg);
}
