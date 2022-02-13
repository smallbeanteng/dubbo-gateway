package com.atommiddleware.cloud.core.security;

import org.owasp.encoder.Encode;
import org.springframework.util.StringUtils;

public class EncodeHtmlXssSecurity implements XssSecurity {

	private String htmlEncode(String origionText) {
		return Encode.forHtmlContent(origionText);
	}

	@Override
	public String xssClean(String origionText) {
		if(StringUtils.isEmpty(origionText)) {
			return origionText;
		}
		return htmlEncode(origionText);
	}

}
