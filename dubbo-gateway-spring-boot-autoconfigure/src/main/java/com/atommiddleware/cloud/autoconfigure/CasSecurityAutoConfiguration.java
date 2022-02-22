package com.atommiddleware.cloud.autoconfigure;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.atommiddleware.cloud.core.cas.CasAjaxAuthenticationEntryPoint;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties.CorsConfig;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties.CsrfConfig;
import com.atommiddleware.cloud.core.security.DefaultPrincipalObtain;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.security.cas.BasedVoter;
import com.atommiddleware.cloud.security.cas.CustomUserDetailsService;
import com.atommiddleware.cloud.security.cas.PrincipalObtain;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.cas", name = "enable", havingValue = "true")
@AutoConfigureAfter(DubboGatewayCommonAutoConfiguration.class)
@ConditionalOnClass({ CasAuthenticationFilter.class })
public class CasSecurityAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationEntryPoint authenticationEntryPoint(ServiceProperties serviceProperties,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, Serialization serialization) {
		CasAjaxAuthenticationEntryPoint entryPoint = new CasAjaxAuthenticationEntryPoint(dubboReferenceConfigProperties,
				serialization);
		entryPoint.setLoginUrl(dubboReferenceConfigProperties.getSecurity().getCas().getServerUrl() + "/login");
		entryPoint.setServiceProperties(serviceProperties);
		return entryPoint;
	}

	@Bean
	@ConditionalOnMissingBean
	protected AuthenticationManager authenticationManager(CasAuthenticationProvider casAuthenticationProvider)
			throws Exception {
		return new ProviderManager(casAuthenticationProvider);
	}

	@Bean
	@ConditionalOnMissingBean
	public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager,
			ServiceProperties serviceProperties) throws Exception {
		CasAuthenticationFilter filter = new CasAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setServiceProperties(serviceProperties);
		return filter;
	}

	@Bean
	@ConditionalOnMissingBean
	public ServiceProperties serviceProperties(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setService(dubboReferenceConfigProperties.getSecurity().getCas().getBaseUrl());
		serviceProperties.setSendRenew(false);
		serviceProperties.setAuthenticateAllArtifacts(true);
		return serviceProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public TicketValidator ticketValidator(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new Cas30ServiceTicketValidator(dubboReferenceConfigProperties.getSecurity().getCas().getServerUrl());
	}

	@Bean
	@ConditionalOnMissingBean
	public CasAuthenticationProvider casAuthenticationProvider(ServiceProperties serviceProperties,
			TicketValidator ticketValidator, CustomUserDetailsService customUserDetailsService) {
		CasAuthenticationProvider provider = new CasAuthenticationProvider();
		provider.setServiceProperties(serviceProperties);
		provider.setTicketValidator(ticketValidator);
		provider.setAuthenticationUserDetailsService(customUserDetailsService);
		provider.setKey("CAS_PROVIDER_LOCALHOST");
		return provider;
	}

	@Bean
	@ConditionalOnMissingBean
	public CustomUserDetailsService customUserDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	@ConditionalOnMissingBean
	public SecurityContextLogoutHandler securityContextLogoutHandler() {
		return new SecurityContextLogoutHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public LogoutFilter logoutFilter(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		LogoutFilter logoutFilter = new LogoutFilter(
				dubboReferenceConfigProperties.getSecurity().getCas().getServerUrl() + "/logout?service="
						+ dubboReferenceConfigProperties.getSecurity().getCas().getBaseUrl(),
				securityContextLogoutHandler());
		logoutFilter.setFilterProcessesUrl("/logout/cas");
		return logoutFilter;
	}

	@Bean
	@ConditionalOnMissingBean
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.setIgnoreInitConfiguration(true);
		return singleSignOutFilter;
	}

	@Bean
	@ConditionalOnMissingBean
	public BasedVoter basedVoter() {
		return new BasedVoter();
	}

	@Bean
	@ConditionalOnMissingBean
	public AccessDecisionManager accessDecisionManager(BasedVoter basedVoter) {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(new WebExpressionVoter(),
				new AuthenticatedVoter(), basedVoter);
		return new UnanimousBased(decisionVoters);
	}

	@Bean
	@ConditionalOnMissingBean
	public PrincipalObtain principalObtain(Serialization serialization,
			DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		return new DefaultPrincipalObtain(serialization,
				dubboReferenceConfigProperties.getSecurity().getCas().getPrincipalAttrs());
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.csrf", name = "enable", havingValue = "true")
	public CsrfTokenRepository csrfTokenRepository(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		CsrfConfig csrfConfig = dubboReferenceConfigProperties.getSecurity().getCsrf();
		CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
		cookieCsrfTokenRepository.setCookieHttpOnly(csrfConfig.isHttpOnly());
		if (!StringUtils.isEmpty(csrfConfig.getDomain())) {
			cookieCsrfTokenRepository.setCookieDomain(csrfConfig.getDomain());
		}
		if (!StringUtils.isEmpty(csrfConfig.getName())) {
			cookieCsrfTokenRepository.setCookieName(csrfConfig.getName());
		}
		if (!StringUtils.isEmpty(csrfConfig.getPath())) {
			cookieCsrfTokenRepository.setCookiePath(csrfConfig.getPath());
		}
		return cookieCsrfTokenRepository;
	}

	@Bean
	@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.cors", name = "enable", havingValue = "true")
	public CorsConfigurationSource corsConfigurationSource(
			DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		CorsConfig cors=dubboReferenceConfigProperties.getSecurity().getCors();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		if (!CollectionUtils.isEmpty(cors.getAllowedHeaders())) {
			configuration.setAllowedHeaders(cors.getAllowedHeaders());
		}
		if (!CollectionUtils.isEmpty(cors.getAllowedOrigins())) {
			configuration.setAllowedOrigins(cors.getAllowedOrigins());
		}
		if (!CollectionUtils.isEmpty(cors.getAllowedMethods())) {
			configuration.setAllowedMethods(cors.getAllowedMethods());
		}
		if (cors.getMaxAge() > 0) {
			configuration
					.setMaxAge(Duration.ofSeconds(cors.getMaxAge()));
		}
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(cors.getPath(),
				configuration);
		return source;
	}
}
