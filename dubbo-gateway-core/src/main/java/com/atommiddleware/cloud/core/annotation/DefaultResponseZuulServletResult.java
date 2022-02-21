package com.atommiddleware.cloud.core.annotation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.netflix.zuul.context.RequestContext;

public class DefaultResponseZuulServletResult implements ResponseZuulServletResult {

	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;
	private final Serialization serialization;

	public DefaultResponseZuulServletResult(DubboReferenceConfigProperties dubboReferenceConfigProperties,
			Serialization serialization) {
		this.dubboReferenceConfigProperties = dubboReferenceConfigProperties;
		this.serialization = serialization;
	}

	@Override
	public Object sevletZuulResponse(String result) {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.setResponseBody(StringUtils.isEmpty(result)?"":result);
		HttpServletResponse httpServletResponse = ctx.getResponse();
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		return null;
	}

	@Override
	public Object sevletZuulResponseException(HttpStatus httpStatus, String msg) {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletResponse httpServletResponse = ctx.getResponse();
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setStatus(httpStatus.value());
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Map<String, Object> map = new HashMap<>();
		map.put("code", httpStatus.value());
		map.put("msg", StringUtils.isEmpty(msg) ? httpStatus.getReasonPhrase() : msg);
		ctx.setResponseBody(serialization.serialize(map));
		return null;
	}

}
