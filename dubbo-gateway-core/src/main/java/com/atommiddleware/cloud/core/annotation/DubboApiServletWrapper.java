package com.atommiddleware.cloud.core.annotation;

import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

public interface DubboApiServletWrapper extends BaseApiWrapper {
	
	CompletableFuture<Object> handler(String pathPattern, HttpServletRequest httpServletRequest, Object body);

}
