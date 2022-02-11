package com.atommiddleware.cloud.core.annotation;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultResponseResult implements ResponseReactiveResult {

	public DefaultResponseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
	
	}

	@Override
	public Mono<Void> reactiveFluxResponse(ServerWebExchange exchange, ServerHttpResponse response,
			Flux<DataBuffer> strDataBuffer) {
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST");
		response.getHeaders().add("Access-Control-Allow-Origin",
				exchange.getRequest().getHeaders().getFirst("Access-Control-Allow-Origin"));
		response.getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
			response.setStatusCode(HttpStatus.OK);
			return response.writeWith(strDataBuffer);
	}

}
