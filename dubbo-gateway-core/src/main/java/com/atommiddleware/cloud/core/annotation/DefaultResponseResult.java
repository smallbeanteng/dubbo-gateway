package com.atommiddleware.cloud.core.annotation;

import java.io.UnsupportedEncodingException;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

public class DefaultResponseResult implements ResponseReactiveResult {

	private final String ERRORRESULT = "{\"code\": 500}";
	private byte[] errorResultByte;

	public DefaultResponseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		try {
			errorResultByte = ERRORRESULT.getBytes(dubboReferenceConfigProperties.getCharset());
		} catch (UnsupportedEncodingException e) {

		}
	}

	@Override
	public Mono<Void> reactiveResponse(ServerWebExchange exchange, ServerHttpResponse response,
			Mono<DataBuffer> strDataBuffer, boolean isErrorResponse) {
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST");
		response.getHeaders().add("Access-Control-Allow-Origin",
				exchange.getRequest().getHeaders().getFirst("Access-Control-Allow-Origin"));
		response.getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
		if (isErrorResponse) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(errorResultByte))));
		} else {
			response.setStatusCode(HttpStatus.OK);
			return response.writeAndFlushWith(ByteBufFlux.just(strDataBuffer));
		}
	}

	@Override
	public Mono<Void> reactiveResponseByte(ServerWebExchange exchange, ServerHttpResponse response,
			byte[] strDataBuffer, boolean isErrorResponse) {
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST");
		response.getHeaders().add("Access-Control-Allow-Origin",
				exchange.getRequest().getHeaders().getFirst("Access-Control-Allow-Origin"));
		response.getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
		if (isErrorResponse) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return response
					.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(errorResultByte))));
		} else {
			response.setStatusCode(HttpStatus.OK);
			return response
					.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(strDataBuffer))));
		}
	}

	@Override
	public Mono<Void> reactiveFluxResponse(ServerWebExchange exchange, ServerHttpResponse response,
			Flux<DataBuffer> strDataBuffer, boolean isErrorResponse) {
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST");
		response.getHeaders().add("Access-Control-Allow-Origin",
				exchange.getRequest().getHeaders().getFirst("Access-Control-Allow-Origin"));
		response.getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
		if (isErrorResponse) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(errorResultByte))));
		} else {
			response.setStatusCode(HttpStatus.OK);
			return response.writeAndFlushWith(ByteBufFlux.just(strDataBuffer));
		}
	}

}
