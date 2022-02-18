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
import org.springframework.core.Ordered;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseServletResult;
import com.atommiddleware.cloud.core.annotation.ResponseServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboServletFilter;
import com.atommiddleware.cloud.core.filter.ServletErrorFilter;
import com.atommiddleware.cloud.core.serialize.Serialization;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnMissingClass(value = { "com.netflix.zuul.http.ZuulServlet", "com.netflix.zuul.http.ZuulServletFilter" })
@AutoConfigureAfter(DubboGatewayCommonAutoConfiguration.class)
public class DubboGatewayServletAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ResponseServletResult responseResult(DubboReferenceConfigProperties dubboReferenceConfigProperties,
			Serialization serialization) {
		return new DefaultResponseServletResult(dubboReferenceConfigProperties, serialization);
	}

	@Bean("registerDubboGatewayFilter")
	@ConditionalOnMissingBean(name = "registerDubboGatewayFilter")
	public FilterRegistrationBean<DubboServletFilter> registerDubboGatewayFilter(
			DubboReferenceConfigProperties dubboReferenceConfigProperties, PathMatcher pathMatcher,
			Serialization serialization, ResponseServletResult responseResult) {
		FilterRegistrationBean<DubboServletFilter> registration = new FilterRegistrationBean<DubboServletFilter>();
		registration.setFilter(
				new DubboServletFilter(pathMatcher, serialization,
						responseResult, dubboReferenceConfigProperties.getExcludUrlPatterns()));
		if (null != dubboReferenceConfigProperties.getIncludUrlPatterns()
				&& dubboReferenceConfigProperties.getIncludUrlPatterns().length > 0) {
			registration.addUrlPatterns(dubboReferenceConfigProperties.getIncludUrlPatterns());
		}
		registration.setName("dubboGatewayFilter");
		registration.setOrder(dubboReferenceConfigProperties.getFilterOrder());
		return registration;
	}

	@Bean("registerServletErrorFilter")
	@ConditionalOnMissingBean(name = "registerServletErrorFilter")
	public FilterRegistrationBean<ServletErrorFilter> registerServletErrorFilter(
			DubboReferenceConfigProperties dubboReferenceConfigProperties, PathMatcher pathMatcher,
			Serialization serialization, ResponseServletResult responseResult) {
		FilterRegistrationBean<ServletErrorFilter> registration = new FilterRegistrationBean<ServletErrorFilter>();
		registration.setFilter(new ServletErrorFilter(responseResult));
		registration.setName("servletErrorFilter");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE - 50);
		return registration;
	}
}
