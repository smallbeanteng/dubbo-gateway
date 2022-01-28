package com.atommiddleware.cloud.core.annotation;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.server.ServerWebExchange;


public interface DubboApiWrapper extends BaseApiWrapper{

	CompletableFuture handler(String pathPattern, ServerWebExchange exchange,Object body);
	
}
