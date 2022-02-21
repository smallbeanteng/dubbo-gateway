package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseZuulServletResult;
import com.atommiddleware.cloud.core.annotation.ResponseZuulServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboServletZuulFilter;
import com.atommiddleware.cloud.core.filter.ZuulErrorFilter;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.security.validation.ParamValidator;
import com.netflix.zuul.filters.ZuulServletFilter;
import com.netflix.zuul.http.ZuulServlet;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ ZuulServlet.class, ZuulServletFilter.class })
@AutoConfigureAfter(DubboGatewayCommonAutoConfiguration.class)
@Import(SevlertImportBeanDefinitionRegistrar.class)
public class DubboGatewayZuulServletAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ResponseZuulServletResult responseZuulServletResult(
			DubboReferenceConfigProperties dubboReferenceConfigProperties, Serialization serialization) {
		return new DefaultResponseZuulServletResult(dubboReferenceConfigProperties, serialization);
	}

	@Bean
	@ConditionalOnMissingBean
	public DubboServletZuulFilter dubboServletZuulFilter(PathMatcher pathMatcher, Serialization serialization,
			ResponseZuulServletResult responseZuulServletResult, ZuulProperties properties,
			ParamValidator paramValidator) {
		return new DubboServletZuulFilter(pathMatcher, serialization, responseZuulServletResult, paramValidator);
	}

	@Bean
	@ConditionalOnMissingBean
	public ZuulErrorFilter dubboZuulErrorFilter(ResponseZuulServletResult responseZuulServletResult) {
		return new ZuulErrorFilter(responseZuulServletResult);
	}
}
