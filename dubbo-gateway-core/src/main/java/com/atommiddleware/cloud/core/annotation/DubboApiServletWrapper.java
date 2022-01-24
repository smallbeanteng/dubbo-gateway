package com.atommiddleware.cloud.core.annotation;

import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;

public interface DubboApiServletWrapper extends BaseApiWrapper {
	
	CompletableFuture handler(String pathPattern, HttpServletRequest httpServletRequest, String body);

}