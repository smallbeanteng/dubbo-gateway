package com.atommiddleware.cloud.autoconfigure;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties.CasConfig;
import com.google.common.collect.Lists;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ WebSecurityConfigurerAdapter.class, CasAuthenticationFilter.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.security.cas", name = "enable", havingValue = "true")
@ConditionalOnMissingBean({ WebSecurityConfigurerAdapter.class })
@AutoConfigureAfter(CasSecurityAutoConfiguration.class)
public class CasSecurityWebSecurityConfigurerAdapterAutoConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private SingleSignOutFilter singleSignOutFilter;
	@Autowired
	private LogoutFilter logoutFilter;
	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;
	@Autowired
	private CasAuthenticationFilter casAuthenticationFilter;
	@Autowired
	private DubboReferenceConfigProperties dubboReferenceConfigProperties;
	@Autowired
	private AccessDecisionManager accessDecisionManager;
	@Autowired(required = false)
	private CsrfTokenRepository csrfTokenRepository;
	@Override
	public void configure(WebSecurity web) throws Exception {
		List<String> ignoringUrls=Lists.newArrayList("/login/cas","/favicon.ico","/error");
		if (!ArrayUtils.isEmpty(dubboReferenceConfigProperties.getSecurity().getCas().getIgnoringUrls())) {
			for(String strIgnoringUrl:dubboReferenceConfigProperties.getSecurity().getCas().getIgnoringUrls()) {
				if(!ignoringUrls.contains(strIgnoringUrl)) {
					ignoringUrls.add(strIgnoringUrl);
				}
			}
		}
		web.ignoring().antMatchers(ignoringUrls.toArray(new String[ignoringUrls.size()]));
		super.configure(web);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CasConfig casConfig=dubboReferenceConfigProperties.getSecurity().getCas();
		if(dubboReferenceConfigProperties.getSecurity().getCors().isEnable()) {
			http.cors();
		}else {
			http.cors().disable();
		}
		List<String> anonymousUrls=Lists.newArrayList("/login/cas","/favicon.ico","/error");
		if(!ArrayUtils.isEmpty(casConfig.getAnonymousUrls())) {
			for(String anonymousUrl:casConfig.getAnonymousUrls()) {
				if(!anonymousUrls.contains(anonymousUrl)) {
					anonymousUrls.add(anonymousUrl);
				}
			}
		}
		http.authorizeRequests().antMatchers(anonymousUrls.toArray(new String[anonymousUrls.size()])).anonymous();
		if(!ArrayUtils.isEmpty(casConfig.getPermitUrls())) {
			http.authorizeRequests().antMatchers(casConfig.getPermitUrls()).permitAll();
		}
		if (!dubboReferenceConfigProperties.getSecurity().getCsrf().isEnable()) {
			http.csrf().disable();
		}
		else {
			if(null!=csrfTokenRepository) {
				http.csrf().csrfTokenRepository(csrfTokenRepository);
			}
		}
		http.authorizeRequests().anyRequest().authenticated().accessDecisionManager(accessDecisionManager).and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
				.addFilterBefore(casAuthenticationFilter, FilterSecurityInterceptor.class).addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
				.addFilterBefore(logoutFilter, LogoutFilter.class);
	}
}
