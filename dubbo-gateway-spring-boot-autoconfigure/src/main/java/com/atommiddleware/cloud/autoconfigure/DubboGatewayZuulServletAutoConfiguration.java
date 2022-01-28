package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.annotation.DefaultResponseServletResult;
import com.atommiddleware.cloud.core.annotation.DefaultResponseZuulServletResult;
import com.atommiddleware.cloud.core.annotation.ResponseServletResult;
import com.atommiddleware.cloud.core.annotation.ResponseZuulServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.filter.DubboServletZuulFilter;
import com.atommiddleware.cloud.core.serialize.JacksonSerialization;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.netflix.zuul.filters.ZuulServletFilter;
import com.netflix.zuul.http.ZuulServlet;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ ZuulServlet.class, ZuulServletFilter.class })
public class DubboGatewayZuulServletAutoConfiguration {

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
	public ResponseZuulServletResult responseZuulServletResult(
			DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultResponseZuulServletResult(dubboReferenceConfigProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public DubboServletZuulFilter dubboServletZuulFilter(DubboReferenceConfigProperties dubboReferenceConfigProperties,
			PathMatcher pathMatcher, Serialization serialization, ResponseZuulServletResult responseZuulServletResult) {
		return new DubboServletZuulFilter(pathMatcher, serialization, dubboReferenceConfigProperties,
				responseZuulServletResult);
	}
}
