package com.atommiddleware.cloud.security.cas;

import java.util.Map;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetailsService extends AbstractCasAssertionUserDetailsService{
    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        // 可自定义获取用户信息
        String username = assertion.getPrincipal().getName();
        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
        return new User(username, "admin", true, true, 
                true, true, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        
        
    }
}
