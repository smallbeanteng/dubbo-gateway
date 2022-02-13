package com.atommiddleware.cloud.core.security;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultXssSecurity implements XssSecurity, InitializingBean {

	private final String DEFALUT_ANTISAMYFILEPATH = "classpath*:antisamy-ebay.xml";
	private final ResourceLoader resourceLoader;
	private final String antisamyFileLocationPattern;
	private Policy policy = null;

	public DefaultXssSecurity(ResourceLoader resourceLoader, String antisamyFileLocationPattern) {
		this.resourceLoader = resourceLoader;
		this.antisamyFileLocationPattern = StringUtils.isEmpty(antisamyFileLocationPattern) ? DEFALUT_ANTISAMYFILEPATH
				: antisamyFileLocationPattern;
	}

	private String antisamyXssClean(String origionText) {
		AntiSamy antiSamy = new AntiSamy();
		try {
			final CleanResults cr = antiSamy.scan(origionText, policy);
			return cr.getCleanHTML();
		} catch (ScanException e) {
			log.error("clean html fail", e);
		} catch (PolicyException e) {
			log.error("clean html policy fail", e);
		}
		return origionText;
	}

	@Override
	public String xssClean(String origionText) {
		if (StringUtils.isEmpty(origionText)) {
			return origionText;
		}
		return antisamyXssClean(origionText);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils
				.getResourcePatternResolver(resourceLoader);
		Resource[] resources = resourcePatternResolver.getResources(antisamyFileLocationPattern);
		for (Resource resource : resources) {
			try {
				policy = Policy.getInstance(resource.getInputStream());
				if (log.isInfoEnabled()) {
					log.info("load antisamyFile path:[{}]", resource.getURL());
				}
				break;
			} catch (PolicyException e) {
				log.error("load policy fail", e);
			}
		}
		if (null == policy) {
			throw new IllegalArgumentException("not find antisamy xml");
		}
	}
}
