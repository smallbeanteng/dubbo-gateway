//package com.atommiddleware.cloud.core.filter;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
//import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.MediaType;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.util.PathMatcher;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//
//import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
//import com.atommiddleware.cloud.core.annotation.DubboApiWrapper;
//import com.atommiddleware.cloud.core.annotation.ResponseReactiveResult;
//import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
//import com.atommiddleware.cloud.core.context.DubboApiContext;
//import com.atommiddleware.cloud.core.serialize.Serialization;
//
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//public class DubboWebFilter implements WebFilter, OrderedWebFilter {
//
//	private final PathMatcher pathMatcher;
//	private final Serialization serialization;
//	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;
//	private final ResponseReactiveResult responseResult;
//	private final int order;
//	private ServerCodecConfigurer serverCodecConfigurer;
//	private final String ERRORRESULT = "{\"code\": 500}";
//	private byte[] errorResultByte;
//
//	public DubboWebFilter(PathMatcher pathMatcher, Serialization serialization,
//			DubboReferenceConfigProperties dubboReferenceConfigProperties, ServerCodecConfigurer serverCodecConfigurer,
//			ResponseReactiveResult responseResult) {
//		this.pathMatcher = pathMatcher;
//		this.serialization = serialization;
//		this.dubboReferenceConfigProperties = dubboReferenceConfigProperties;
//		this.order = dubboReferenceConfigProperties.getFilterOrder();
//		this.serverCodecConfigurer = serverCodecConfigurer;
//		this.responseResult = responseResult;
//		try {
//			errorResultByte = ERRORRESULT.getBytes(dubboReferenceConfigProperties.getCharset());
//		} catch (UnsupportedEncodingException e) {
//
//		}
//	}
//
//	private Mono<Void> response500(ServerWebExchange exchange, ServerHttpResponse response) {
//		return responseResult.reactiveResponse(exchange, response, null, true);
//	}
//
//	@Override
//	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//		String path = exchange.getRequest().getPath().value();
//		String pathPatternTemp = path;
//		DubboApiWrapper dubboApiWrapperTemp = DubboApiContext.MAP_DUBBO_API_WRAPPER.get(path);
//		if (null == dubboApiWrapperTemp) {
//			for (Map.Entry<String, DubboApiWrapper> entry : DubboApiContext.MAP_DUBBO_API_PATH_PATTERN_WRAPPER
//					.entrySet()) {
//				if (pathMatcher.match(entry.getKey(), path)) {
//					dubboApiWrapperTemp = entry.getValue();
//					pathPatternTemp = entry.getKey();
//					break;
//				}
//			}
//		}
//		if (null == dubboApiWrapperTemp) {
//			return chain.filter(exchange);
//		} else {
//			final String pathPattern = pathPatternTemp;
//			RequestMethod requestMethod = DubboApiContext.PATTERNS_REQUESTMETHOD.get(pathPattern);
//			ServerHttpResponse response = exchange.getResponse();
//			String httpMethodName = exchange.getRequest().getMethod().name();
//			if (!httpMethodName.equals(requestMethod.name())) {
//				log.error("path:[{}] requestMethod is fail PathMapping requestMethod:[{}]", pathPattern,
//						requestMethod.name());
//				return response500(exchange, response);
//			} else {
//				final DubboApiWrapper dubboApiWrapper = dubboApiWrapperTemp;
//
//				// 只接收get 和post 请求 减少复杂性
//				if (httpMethodName.equals(RequestMethod.POST.name())) {
//					if (!exchange.getRequest().getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)) {
//						log.error("path:[{}] body param media must application/json", pathPattern);
//						return response500(exchange, response);
//					}
//					ServerRequest serverRequest = ServerRequest.create(exchange, serverCodecConfigurer.getReaders());
//					Mono<DataBuffer> strm = serverRequest.bodyToMono(DataBuffer.class).flatMap(body -> {
//						byte[] bytes = new byte[body.readableByteCount()];
//						body.read(bytes);
//						DataBufferUtils.release(body);
//						String bodyString = null;
//						try {
//							bodyString = new String(bytes, dubboReferenceConfigProperties.getCharset());
//						} catch (UnsupportedEncodingException e) {
//							log.error("fai", e);
//							return Mono.just(response.bufferFactory().wrap(errorResultByte));
//						}
//						CompletableFuture<Object> completableFuture;
//						try {
//							// 异步处理
//							completableFuture = dubboApiWrapper.handler(pathPattern, exchange, bodyString);
//						} catch (Exception ex) {
//							log.error("fail to access dubbo path:[" + pathPattern + "]", ex);
//							return Mono.just(response.bufferFactory().wrap(errorResultByte));
//						}
//						return Mono.fromFuture(completableFuture).flatMap(o -> {
//							try {
//								return Mono.just(response.bufferFactory().wrap(serialization.serialize(o)
//										.getBytes(dubboReferenceConfigProperties.getCharset())));
//							} catch (UnsupportedEncodingException e) {
//								log.error("fail access path:{}", pathPattern);
//							}
//							return Mono.just(response.bufferFactory().wrap(errorResultByte));
//						});
//					});
//					return responseResult.reactiveResponse(exchange, response, strm, false);
//				} else if (httpMethodName.equals(RequestMethod.GET.name())) {
//					final CompletableFuture<Object> completableFuture;
//					try {
//						completableFuture = dubboApiWrapper.handler(pathPattern, exchange, null);
//					} catch (Exception ex) {
//						log.error("fail to access dubbo path:[" + pathPattern + "]", ex);
//						return response500(exchange, response);
//					}
//					Mono<DataBuffer> strm = Mono.fromFuture(completableFuture).flatMap(o -> {
//						try {
//							return Mono.just(response.bufferFactory().wrap(
//									serialization.serialize(o).getBytes(dubboReferenceConfigProperties.getCharset())));
//						} catch (UnsupportedEncodingException e) {
//							log.error("fail access path:[{}]", pathPattern);
//						}
//						return Mono.just(response.bufferFactory().wrap(errorResultByte));
//					});
//
//					return responseResult.reactiveResponse(exchange, response, strm, false);
//				} else {
//					log.error("Only get and post are supported for the time being path:[{}] requestMethod:[{}]", pathPattern, requestMethod.name());
//					return response500(exchange, response);
//				}
//			}
//		}
//	}
//
//	@Override
//	public int getOrder() {
//		return this.order;
//	}
//}
