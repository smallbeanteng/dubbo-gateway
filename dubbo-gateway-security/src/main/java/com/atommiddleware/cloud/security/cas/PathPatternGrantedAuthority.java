package com.atommiddleware.cloud.security.cas;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathPatternGrantedAuthority implements GrantedAuthority{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String pathPattern;
	private final static PathMatcher pathMatcher=new AntPathMatcher();
	
	public PathPatternGrantedAuthority(String pathPathPattern) {
		this.pathPattern=pathPathPattern;
	}
	
	public String getPathPattern() {
		return pathPattern;
	}

	@Override
	public String getAuthority() {
		return pathPattern;
	}
	
	public boolean match(String path) {
		return pathMatcher.match(pathPattern, path);
	}

}
