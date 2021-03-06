package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.security.DefaultXssSecurity;
import com.atommiddleware.cloud.core.security.EncodeHtmlXssSecurity;
import com.atommiddleware.cloud.core.security.EsapiEncodeHtmlXssSecurity;
import com.atommiddleware.cloud.core.security.XssSecurity;
import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterStrategy;
import com.atommiddleware.cloud.core.serialize.JacksonSerialization;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.security.validation.DefaultParamValidator;
import com.atommiddleware.cloud.security.validation.ParamValidator;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(name = "org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
public class DubboGatewayCommonAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.xss", name = "filterMode", havingValue = "0", matchIfMissing = true)
	public XssSecurity xssSecurityEsapiEncodeHtml() {
		return new EsapiEncodeHtmlXssSecurity();
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.xss", name = "filterMode", havingValue = "1")
	public XssSecurity xssSecurity(ResourceLoader resourceLoader,
			DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultXssSecurity(resourceLoader,
				dubboReferenceConfigProperties.getSecurity().getXss().getAntisamyFileLocationPattern());
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.xss", name = "filterMode", havingValue = "2")
	public XssSecurity xssSecurityEncodeHtml() {
		return new EncodeHtmlXssSecurity();
	}

	@Bean
	@ConditionalOnMissingBean
	public Serialization serialization(DubboReferenceConfigProperties dubboReferenceConfigProperties,
			XssSecurity xssSecurity) {
		return new JacksonSerialization(dubboReferenceConfigProperties.getSecurity().getXss().isEnable(), xssSecurity,
				XssFilterStrategy.values()[dubboReferenceConfigProperties.getSecurity().getXss().getFilterStrategy()]);
	}

	@Bean
	@ConditionalOnMissingBean
	public PathMatcher pathMatcher() {
		return new AntPathMatcher();
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "javax.validation.ConstraintViolationException")
	public ParamValidator paramValidator() {
		return new DefaultParamValidator();
	}
}
