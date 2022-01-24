package com.atommiddleware.cloud.core.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.ResponseReactiveResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.serialize.Serialization;

public class DubboGatewayFilterFactory extends AbstractGatewayFilterFactory<DubboGatewayFilterFactory.Config> {

	private final DubboGatewayFilter dubboGatewayFilter;

	public DubboGatewayFilterFactory(PathMatcher pathMatcher, Serialization serialization,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, ServerCodecConfigurer serverCodecConfigurer,
			ResponseReactiveResult responseResult) {
		super(Config.class);
		this.dubboGatewayFilter = new DubboGatewayFilter(pathMatcher, serialization, dubboReferenceConfigProperties,
				serverCodecConfigurer, responseResult);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			return dubboGatewayFilter.filter(exchange, chain);
		});
	}

	public static class Config {

	}
}
