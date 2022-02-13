package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseServletResult;
import com.atommiddleware.cloud.core.annotation.ResponseServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboServletFilter;
import com.atommiddleware.cloud.core.serialize.Serialization;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnMissingClass(value = { "com.netflix.zuul.http.ZuulServlet", "com.netflix.zuul.http.ZuulServletFilter" })
@AutoConfigureAfter(DubboGatewayCommonAutoConfiguration.class)
public class DubboGatewayServletAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ResponseServletResult responseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties,Serialization serialization) {
		return new DefaultResponseServletResult(dubboReferenceConfigProperties,serialization);
	}

	@Bean
	@ConditionalOnMissingBean
	public FilterRegistrationBean<DubboServletFilter> registerDubboGatewayFilter(
			DubboReferenceConfigProperties dubboReferenceConfigProperties, PathMatcher pathMatcher,
			Serialization serialization, ResponseServletResult responseResult) {
		FilterRegistrationBean<DubboServletFilter> registration = new FilterRegistrationBean<DubboServletFilter>();
		registration.setFilter(
				new DubboServletFilter(pathMatcher, serialization, dubboReferenceConfigProperties.getFilterOrder(), responseResult,dubboReferenceConfigProperties.getExcludUrlPatterns()));
		if(null!=dubboReferenceConfigProperties.getIncludUrlPatterns()&&dubboReferenceConfigProperties.getIncludUrlPatterns().length>0) {
		registration.addUrlPatterns(dubboReferenceConfigProperties.getIncludUrlPatterns());
		}
		registration.setName("dubboGatewayFilter");
		registration.setOrder(dubboReferenceConfigProperties.getFilterOrder());
		return registration;
	}
}
