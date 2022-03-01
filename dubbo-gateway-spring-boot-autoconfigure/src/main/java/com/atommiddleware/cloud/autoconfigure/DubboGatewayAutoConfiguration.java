package com.atommiddleware.cloud.autoconfigure;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseResult;
import com.atommiddleware.cloud.core.annotation.ResponseReactiveResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboGlobalFilter;
import com.atommiddleware.cloud.core.serialize.Serialization;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(DubboGatewayCommonAutoConfiguration.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass({ GlobalFilter.class, DubboReference.class })
public class DubboGatewayAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ResponseReactiveResult responseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultResponseResult(dubboReferenceConfigProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public DubboGlobalFilter dubboGlobalFilter(ServerCodecConfigurer serverCodecConfigurer,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, ResponseReactiveResult responseResult,
			Serialization serialization, PathMatcher pathMatcher) {
		return new DubboGlobalFilter(pathMatcher, serialization, dubboReferenceConfigProperties, serverCodecConfigurer,
				responseResult);
	}
}
