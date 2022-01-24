package com.atommiddleware.cloud.core.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResponseServletResult {
	public void sevletResponse(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,String result,boolean isErrorResponse);
}
