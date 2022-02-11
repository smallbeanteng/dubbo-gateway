package com.atommiddleware.cloud.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

@ConfigurationProperties(prefix = "com.atommiddleware.cloud.config")
public class DubboReferenceConfigProperties {
	
	private String charset="UTF-8";
	
	private int filterOrder=Ordered.LOWEST_PRECEDENCE;
	
	private Map<String,DubboReferenceConfig> dubboRefer=new HashMap<String, DubboReferenceConfig>();

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

}
