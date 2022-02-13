package com.atommiddleware.cloud.core.security;

import org.owasp.esapi.ESAPI;
import org.springframework.util.StringUtils;

public class EsapiEncodeHtmlXssSecurity implements XssSecurity {

	@Override
	public String xssClean(String origionText) {
		if (StringUtils.isEmpty(origionText)) {
			return origionText;
		}
		return ESAPI.encoder().encodeForHTML(origionText);
	}

}
