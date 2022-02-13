package com.atommiddleware.cloud.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterStrategy;
import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterType;

@ConfigurationProperties(prefix = "com.atommiddleware.cloud.config")
public class DubboReferenceConfigProperties {

	private String charset = "UTF-8";

	private int filterOrder = Ordered.LOWEST_PRECEDENCE;

	private Map<String, DubboReferenceConfig> dubboRefer = new HashMap<String, DubboReferenceConfig>();

	private SecurityConfig securityConfig = new SecurityConfig();

	public SecurityConfig getSecurityConfig() {
		return securityConfig;
	}

	public void setSecurityConfig(SecurityConfig securityConfig) {
		this.securityConfig = securityConfig;
	}

	@Value("${includUrlPatterns:#{null}}")
	private String[] includUrlPatterns;

	@Value("${excludUrlPatterns:#{null}}")
	private String[] excludUrlPatterns;

	public String[] getIncludUrlPatterns() {
		return includUrlPatterns;
	}

	public void setIncludUrlPatterns(String[] includUrlPatterns) {
		this.includUrlPatterns = includUrlPatterns;
	}

	public String[] getExcludUrlPatterns() {
		return excludUrlPatterns;
	}

	public void setExcludUrlPatterns(String[] excludUrlPatterns) {
		this.excludUrlPatterns = excludUrlPatterns;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getFilterOrder() {
		return filterOrder;
	}

	public void setFilterOrder(int filterOrder) {
		this.filterOrder = filterOrder;
	}

	public Map<String, DubboReferenceConfig> getDubboRefer() {
		return dubboRefer;
	}

	public void setDubboRefer(Map<String, DubboReferenceConfig> dubboRefer) {
		this.dubboRefer = dubboRefer;
	}

	public class SecurityConfig {

		private boolean xssFilterEnable = true;

		private String antisamyFileLocationPattern;
		// response 0 request 1 
		private int xssFilterStrategy = XssFilterStrategy.RESPONSE.ordinal();
		// 0 anti 1 esapi 2 encodehtml
		private int xssFilterType = XssFilterType.ANTISAMY.ordinal();

		public int getXssFilterType() {
			return xssFilterType;
		}

		public void setXssFilterType(int xssFilterType) {
			this.xssFilterType = xssFilterType;
		}

		public int getXssFilterStrategy() {
			return xssFilterStrategy;
		}

		public void setXssFilterStrategy(int xssFilterStrategy) {
			this.xssFilterStrategy = xssFilterStrategy;
		}

		public String getAntisamyFileLocationPattern() {
			return antisamyFileLocationPattern;
		}

		public void setAntisamyFileLocationPattern(String antisamyFileLocationPattern) {
			this.antisamyFileLocationPattern = antisamyFileLocationPattern;
		}

		public boolean isXssFilterEnable() {
			return xssFilterEnable;
		}

		public void setXssFilterEnable(boolean xssFilterEnable) {
			this.xssFilterEnable = xssFilterEnable;
		}

	}
}
