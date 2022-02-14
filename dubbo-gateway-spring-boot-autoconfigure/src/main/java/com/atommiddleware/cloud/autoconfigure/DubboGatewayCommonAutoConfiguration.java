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
import com.atommiddleware.cloud.security.validation.ParamValidator.ValidatorMode;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(name = "org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
public class DubboGatewayCommonAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.securityConfig", name = "xssFilterType", havingValue = "0", matchIfMissing = true)
	public XssSecurity xssSecurity(ResourceLoader resourceLoader,
			DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultXssSecurity(resourceLoader,
				dubboReferenceConfigProperties.getSecurityConfig().getAntisamyFileLocationPattern());
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.securityConfig", name = "xssFilterType", havingValue = "1")
	public XssSecurity xssSecurityEsapiEncodeHtml() {
		return new EsapiEncodeHtmlXssSecurity();
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.securityConfig", name = "xssFilterType", havingValue = "2")
	public XssSecurity xssSecurityEncodeHtml() {
		return new EncodeHtmlXssSecurity();
	}

	@Bean
	@ConditionalOnMissingBean
	public Serialization serialization(DubboReferenceConfigProperties dubboReferenceConfigProperties,
			XssSecurity xssSecurity) {
		return new JacksonSerialization(dubboReferenceConfigProperties.getSecurityConfig().isXssFilterEnable(),
				xssSecurity,
				XssFilterStrategy.values()[dubboReferenceConfigProperties.getSecurityConfig().getXssFilterStrategy()]);
	}

	@Bean
	@ConditionalOnMissingBean
	public PathMatcher pathMatcher() {
		return new AntPathMatcher();
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.securityConfig", name = "validateParamEnable", havingValue = "true", matchIfMissing = true)
	@ConditionalOnClass(name = "org.hibernate.validator.constraints.Length")
	public ParamValidator paramValidator(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultParamValidator(
				ValidatorMode.values()[dubboReferenceConfigProperties.getSecurityConfig().getValidatorMode()]);
	}
}
