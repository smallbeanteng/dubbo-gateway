package com.atommiddleware.cloud.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

@ConfigurationProperties(prefix = "com.atommiddleware.cloud.config")
public class DubboReferenceConfigProperties {
	
	private String charset="UTF-8";
	
	private int filterOrder=Ordered.LOWEST_PRECEDENCE;
	
	private String filterUrlPatterns="/*";
	
	private Map<String,DubboReferenceConfig> dubboRefer=new HashMap<String, DubboReferenceConfig>();

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

	public String getFilterUrlPatterns() {
		return filterUrlPatterns;
	}

	public void setFilterUrlPatterns(String filterUrlPatterns) {
		this.filterUrlPatterns = filterUrlPatterns;
	}

	public Map<String, DubboReferenceConfig> getDubboRefer() {
		return dubboRefer;
	}

	public void setDubboRefer(Map<String, DubboReferenceConfig> dubboRefer) {
		this.dubboRefer = dubboRefer;
	}

}
