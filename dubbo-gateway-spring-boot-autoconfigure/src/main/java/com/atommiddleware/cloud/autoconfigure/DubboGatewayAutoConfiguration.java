package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseResult;
import com.atommiddleware.cloud.core.annotation.ResponseReactiveResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboWebFilter;
import com.atommiddleware.cloud.core.serialize.JacksonSerialization;
import com.atommiddleware.cloud.core.serialize.Serialization;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(name = "org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class DubboGatewayAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Serialization serialization() {
		return new JacksonSerialization();
	}

	@Bean
	@ConditionalOnMissingBean
	public PathMatcher pathMatcher() {
		return new AntPathMatcher();
	}

	@Bean
	@ConditionalOnMissingBean
	public ResponseReactiveResult responseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultResponseResult(dubboReferenceConfigProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public DubboWebFilter dubboWebFilter(ServerCodecConfigurer serverCodecConfigurer,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, ResponseReactiveResult responseResult) {
		return new DubboWebFilter(pathMatcher(), serialization(), dubboReferenceConfigProperties, serverCodecConfigurer,
				responseResult);
	}

}
