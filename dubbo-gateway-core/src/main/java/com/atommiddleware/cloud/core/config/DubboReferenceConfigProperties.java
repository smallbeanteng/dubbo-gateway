package com.atommiddleware.cloud.core.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterMode;
import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterStrategy;

@ConfigurationProperties(prefix = "com.atommiddleware.cloud.config")
public class DubboReferenceConfigProperties {

	private String charset = "UTF-8";

	private int filterOrder = Ordered.LOWEST_PRECEDENCE;

	private Map<String, DubboReferenceConfig> dubboRefer = new HashMap<String, DubboReferenceConfig>();

	private SecurityConfig security = new SecurityConfig();

	private RedisHttpSessionConfig session = new RedisHttpSessionConfig();

	public SecurityConfig getSecurity() {
		return security;
	}

	public void setSecurity(SecurityConfig security) {
		this.security = security;
	}

	public RedisHttpSessionConfig getSession() {
		return session;
	}

	public void setSession(RedisHttpSessionConfig session) {
		this.session = session;
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

	public class RedisHttpSessionConfig {
		private CookieConfig cookie = new CookieConfig();

		public CookieConfig getCookie() {
			return cookie;
		}

		public void setCookie(CookieConfig cookie) {
			this.cookie = cookie;
		}

		public class CookieConfig {
			private String domain;
			private String name;
			private String path;
			private boolean enable;

			public String getDomain() {
				return domain;
			}

			public void setDomain(String domain) {
				this.domain = domain;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getPath() {
				return path;
			}

			public void setPath(String path) {
				this.path = path;
			}

			public boolean isEnable() {
				return enable;
			}

			public void setEnable(boolean enable) {
				this.enable = enable;
			}

		}

	}

	public class SecurityConfig {

		private CasConfig cas = new CasConfig();
		private XssConfig xss = new XssConfig();
		private CsrfConfig csrf = new CsrfConfig();
		private CorsConfig cors=new CorsConfig();
		
		public CorsConfig getCors() {
			return cors;
		}

		public void setCors(CorsConfig cors) {
			this.cors = cors;
		}

		public CasConfig getCas() {
			return cas;
		}

		public void setCas(CasConfig cas) {
			this.cas = cas;
		}

		public XssConfig getXss() {
			return xss;
		}

		public void setXss(XssConfig xss) {
			this.xss = xss;
		}

		public CsrfConfig getCsrf() {
			return csrf;
		}

		public void setCsrf(CsrfConfig csrf) {
			this.csrf = csrf;
		}

	}
	public class CorsConfig{
		/**
		 * ????????????cors
		 */
		private boolean enable=true;
		/**
		 * ???????????????????????????
		 */
		@Value("${allowedOrigins:#{null}}")
		private List<String> allowedOrigins;
		/**
		 * ?????????????????????
		 */
		@Value("${allowedMethods:#{null}}")
		private List<String> allowedMethods;
		/**
		 * ??????????????????
		 */
		@Value("${allowedHeaders:#{null}}")
		private List<String> allowedHeaders;
		/**
		 * ?????????????????????path
		 */
		private String path="/**";
		/**
		 * ???????????????
		 */
		private int maxAge=600;
		
		public int getMaxAge() {
			return maxAge;
		}
		public void setMaxAge(int maxAge) {
			this.maxAge = maxAge;
		}
		public boolean isEnable() {
			return enable;
		}
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		public List<String> getAllowedOrigins() {
			return allowedOrigins;
		}
		public void setAllowedOrigins(List<String> allowedOrigins) {
			this.allowedOrigins = allowedOrigins;
		}
		public List<String> getAllowedMethods() {
			return allowedMethods;
		}
		public void setAllowedMethods(List<String> allowedMethods) {
			this.allowedMethods = allowedMethods;
		}
		public List<String> getAllowedHeaders() {
			return allowedHeaders;
		}
		public void setAllowedHeaders(List<String> allowedHeaders) {
			this.allowedHeaders = allowedHeaders;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		
	}
	public class CasConfig {
		/**
		 * ????????????cas
		 */
		private boolean enable = false;
		/**
		 * ????????????????????????
		 */
		private String baseUrl;
		/**
		 * ????????????????????????
		 */
		private String serverUrl;
		/**
		 * ????????????????????????????????????????????????
		 */
		@Value("${principalAttrs:#{null}}")
		private List<String> principalAttrs;
		/**
		 * ??????????????????
		 */
		@Value("${ignoringUrls:#{null}}")
		private String[] ignoringUrls;
		/**
		 * ????????????????????????Pattern
		 */
		@Value("${permitUrls:#{null}}")
		private String[] permitUrls;
		/**
		 * ????????????????????????Pattern
		 */
		@Value("${anonymousUrls:#{null}}")
		private String[] anonymousUrls;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getServerUrl() {
			return serverUrl;
		}

		public void setServerUrl(String serverUrl) {
			this.serverUrl = serverUrl;
		}

		public List<String> getPrincipalAttrs() {
			return principalAttrs;
		}

		public void setPrincipalAttrs(List<String> principalAttrs) {
			this.principalAttrs = principalAttrs;
		}

		public String[] getIgnoringUrls() {
			return ignoringUrls;
		}

		public void setIgnoringUrls(String[] ignoringUrls) {
			this.ignoringUrls = ignoringUrls;
		}

		public String[] getPermitUrls() {
			return permitUrls;
		}

		public void setPermitUrls(String[] permitUrls) {
			this.permitUrls = permitUrls;
		}

		public String[] getAnonymousUrls() {
			return anonymousUrls;
		}

		public void setAnonymousUrls(String[] anonymousUrls) {
			this.anonymousUrls = anonymousUrls;
		}

	}

	public class XssConfig {
		/**
		 * ????????????xss????????????
		 */
		private boolean enable = true;
		/**
		 * ???????????? 0 ?????? 1 ??????
		 */
		private int filterStrategy = XssFilterStrategy.RESPONSE.ordinal();
		/**
		 * ???????????? 0 html???????????? 1 ?????? 2 html??????
		 */
		private int filterMode = XssFilterMode.ESAPI.ordinal();
		/**
		 * ???????????????????????? ?????? antisamy-ebay.xml
		 */
		private String antisamyFileLocationPattern;

		public int getFilterMode() {
			return filterMode;
		}

		public void setFilterMode(int filterMode) {
			this.filterMode = filterMode;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public int getFilterStrategy() {
			return filterStrategy;
		}

		public void setFilterStrategy(int filterStrategy) {
			this.filterStrategy = filterStrategy;
		}

		public String getAntisamyFileLocationPattern() {
			return antisamyFileLocationPattern;
		}

		public void setAntisamyFileLocationPattern(String antisamyFileLocationPattern) {
			this.antisamyFileLocationPattern = antisamyFileLocationPattern;
		}

	}

	public class CsrfConfig {
		/**
		 * csrf ?????? ????????????
		 */
		private boolean enable = true;
		
		private String domain;
		private String name;
		private String path;
		private boolean httpOnly=false;
		
		public boolean isHttpOnly() {
			return httpOnly;
		}

		public void setHttpOnly(boolean httpOnly) {
			this.httpOnly = httpOnly;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

	}
}
