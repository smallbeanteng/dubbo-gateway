package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseServletResult;
import com.atommiddleware.cloud.core.annotation.ResponseServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboFilter;
import com.atommiddleware.cloud.core.serialize.JacksonSerialization;
import com.atommiddleware.cloud.core.serialize.Serialization;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = Type.SERVLET)
public class DubboGatewayServletAutoConfiguration {

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
	public ResponseServletResult responseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultResponseServletResult(dubboReferenceConfigProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public FilterRegistrationBean registerDubboGatewayFilter(DubboReferenceConfigProperties dubboReferenceConfigProperties,PathMatcher pathMatcher,Serialization serialization,ResponseServletResult responseResult) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new DubboFilter(pathMatcher, serialization, dubboReferenceConfigProperties, responseResult));
		registration.addUrlPatterns(dubboReferenceConfigProperties.getFilterUrlPatterns());
		registration.setName("dubboGatewayFilter");
		registration.setOrder(dubboReferenceConfigProperties.getFilterOrder());
		return registration;
	}
}
