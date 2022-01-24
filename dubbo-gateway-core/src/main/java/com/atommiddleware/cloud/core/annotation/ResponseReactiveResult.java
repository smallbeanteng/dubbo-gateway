package com.atommiddleware.cloud.core.annotation;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseReactiveResult {

	public Mono<Void> reactiveResponse(ServerWebExchange exchange,ServerHttpResponse response, Mono<DataBuffer> strDataBuffer,boolean isErrorResponse);
	
	public Mono<Void> reactiveFluxResponse(ServerWebExchange exchange,ServerHttpResponse response, Flux<DataBuffer> strDataBuffer,boolean isErrorResponse);
	
	public Mono<Void> reactiveResponseByte(ServerWebExchange exchange,ServerHttpResponse response,byte[] strDataBuffer,boolean isErrorResponse);
}
