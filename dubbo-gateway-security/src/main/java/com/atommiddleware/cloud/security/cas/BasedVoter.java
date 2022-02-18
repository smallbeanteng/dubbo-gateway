package com.atommiddleware.cloud.security.cas;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class BasedVoter implements AccessDecisionVoter<Object> { 
	
	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		int result = ACCESS_DENIED;
		if (authenticationTrustResolver.isAnonymous(authentication)) {
			return ACCESS_GRANTED;
		}
		if(null==authentication) {
			return result;
		}
		return vodeHandle(authentication,object,attributes);
	
	}

	protected int vodeHandle(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		int result = ACCESS_DENIED;
		Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);
		for (GrantedAuthority authority : authorities) {
			if (authority instanceof SimpleGrantedAuthority) {
				SimpleGrantedAuthority simpleGrantedAuthority = (SimpleGrantedAuthority) authority;
				if (simpleGrantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
					return ACCESS_GRANTED;
				}
			}
		}
		return result;
	}
	
	protected Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
		return authentication.getAuthorities();
	}
}
