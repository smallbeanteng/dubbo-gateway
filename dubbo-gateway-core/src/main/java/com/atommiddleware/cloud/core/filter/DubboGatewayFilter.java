package com.atommiddleware.cloud.core.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.PathMatcher;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.core.annotation.DubboApiWrapper;
import com.atommiddleware.cloud.core.annotation.ResponseReactiveResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.serialize.Serialization;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class DubboGatewayFilter implements GatewayFilter, Ordered {
	private final PathMatcher pathMatcher;
	private final Serialization serialization;
	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;
	private final ResponseReactiveResult responseResult;
	private final int order;
	private ServerCodecConfigurer serverCodecConfigurer;
	private final String ERRORRESULT = "{\"code\": 500}";
	private byte[] errorResultByte;

	public DubboGatewayFilter(PathMatcher pathMatcher, Serialization serialization,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, ServerCodecConfigurer serverCodecConfigurer,
			ResponseReactiveResult responseResult) {
		this.pathMatcher = pathMatcher;
		this.serialization = serialization;
		this.dubboReferenceConfigProperties = dubboReferenceConfigProperties;
		this.order = dubboReferenceConfigProperties.getFilterOrder();
		this.serverCodecConfigurer = serverCodecConfigurer;
		this.responseResult = responseResult;
		try {
			errorResultByte = ERRORRESULT.getBytes(dubboReferenceConfigProperties.getCharset());
		} catch (UnsupportedEncodingException e) {

		}
	}

	private Mono<Void> response500(ServerWebExchange exchange, ServerHttpResponse response) {
		return responseResult.reactiveResponse(exchange, response, null, true);
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getPath().value();
		String pathPatternTemp = path;
		DubboApiWrapper dubboApiWrapperTemp = DubboApiContext.MAP_DUBBO_API_WRAPPER.get(path);
		if (null == dubboApiWrapperTemp) {
			for (Map.Entry<String, DubboApiWrapper> entry : DubboApiContext.MAP_DUBBO_API_PATH_PATTERN_WRAPPER
					.entrySet()) {
				if (pathMatcher.match(entry.getKey(), path)) {
					dubboApiWrapperTemp = entry.getValue();
					pathPatternTemp = entry.getKey();
					break;
				}
			}
		}
		if (null == dubboApiWrapperTemp) {
			return chain.filter(exchange);
		} else {
			final String pathPattern = pathPatternTemp;
			RequestMethod requestMethod = DubboApiContext.PATTERNS_REQUESTMETHOD.get(pathPattern);
			ServerHttpResponse response = exchange.getResponse();
			String httpMethodName = exchange.getRequest().getMethod().name();
			if (!httpMethodName.equals(requestMethod.name())) {
				log.error("path:[{}] requestMethod is fail PathMapping requestMethod:[{}]", pathPattern,
						requestMethod.name());
				return response500(exchange, response);
			} else {
				final DubboApiWrapper dubboApiWrapper = dubboApiWrapperTemp;
				if (httpMethodName.equals(RequestMethod.POST.name())) {
					if (exchange.getRequest().getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)
							|| exchange.getRequest().getHeaders().getContentType()
									.equals(MediaType.APPLICATION_JSON_UTF8)) {
						Object attrBody = exchange.getAttribute(CACHED_REQUEST_BODY_ATTR);
						if (null != attrBody) {
							NettyDataBuffer nettyDataBuffer = (NettyDataBuffer) attrBody;
							return responseResult.reactiveFluxResponse(exchange, response,
									Flux.just(nettyDataBuffer).flatMap(o -> {
										byte[] bytes = new byte[o.readableByteCount()];
										o.read(bytes);
										DataBufferUtils.release(o);
										String bodyString = null;
										try {
											bodyString = new String(bytes, dubboReferenceConfigProperties.getCharset());
										} catch (UnsupportedEncodingException e) {
											log.error("fai", e);
											return Mono.just(response.bufferFactory().wrap(errorResultByte));
										}

										return Mono
												.fromFuture(dubboApiWrapper.handler(pathPattern, exchange, bodyString))
												.flatMap(k -> {
													return Mono.just(response.bufferFactory()
															.wrap(serialization.serializeByte(k)));
												});
									}), false);
						} else {
							ServerRequest serverRequest = ServerRequest.create(exchange,
									serverCodecConfigurer.getReaders());
							return responseResult.reactiveFluxResponse(exchange, response,
									Flux.just(serverRequest.bodyToMono(DataBuffer.class)).flatMap(u -> {
										return u.flatMap(o -> {
											byte[] bytes = new byte[o.readableByteCount()];
											o.read(bytes);
											DataBufferUtils.release(o);
											String bodyString = null;
											try {
												bodyString = new String(bytes,
														dubboReferenceConfigProperties.getCharset());
											} catch (UnsupportedEncodingException e) {
												log.error("fai", e);
												return Mono.just(response.bufferFactory().wrap(errorResultByte));
											}
											return Mono
													.fromFuture(
															dubboApiWrapper.handler(pathPattern, exchange, bodyString))
													.flatMap(k -> {
														return Mono.just(response.bufferFactory()
																.wrap(serialization.serializeByte(k)));
													});
										});
									}), false);
						}
					} else if (exchange.getRequest().getHeaders().getContentType()
							.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
						// form表单形式
						ServerRequest serverRequest = ServerRequest.create(exchange,
								serverCodecConfigurer.getReaders());
						Flux<DataBuffer> fx = Flux.from(serverRequest.formData().flatMap(o -> {
							return Mono.fromFuture(dubboApiWrapper.handler(pathPattern, exchange, o)).flatMap(k -> {
								return Mono.just(response.bufferFactory().wrap(serialization.serializeByte(k)));
							});
						}));
						return responseResult.reactiveFluxResponse(exchange, response, fx, false);
					} else {
						log.error("path:[{}] body param media must application/json", pathPattern);
						return response500(exchange, response);
					}
				} else if (httpMethodName.equals(RequestMethod.GET.name())) {
					Flux<DataBuffer> fx = Flux
							.from(Mono.fromFuture(dubboApiWrapper.handler(pathPattern, exchange, null)).flatMap(k -> {
								return Mono.just(response.bufferFactory().wrap(serialization.serializeByte(k)));
							}));

					return responseResult.reactiveFluxResponse(exchange, response, fx, false);
				} else {
					log.error("Only get and post are supported for the time being path:[{}] requestMethod:[{}]",
							pathPattern, requestMethod.name());
					return response500(exchange, response);
				}
			}
		}
	}

}
