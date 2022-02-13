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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultResponseZuulServletResult implements ResponseZuulServletResult {

	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;
    private final Serialization serialization;
	public DefaultResponseZuulServletResult(DubboReferenceConfigProperties dubboReferenceConfigProperties,Serialization serialization) {
		this.dubboReferenceConfigProperties = dubboReferenceConfigProperties;
		this.serialization=serialization;
	}

	@Override
	public Object sevletZuulResponse(String result) {
		RequestContext ctx = RequestContext.getCurrentContext();
		// zuul 网关直接返回响应，不让请求访问后续的接口
		ctx.setSendZuulResponse(false);
		HttpServletResponse httpServletResponse=ctx.getResponse();
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		try {
			if(StringUtils.isEmpty(result)) {
            	result="";
            }
			httpServletResponse.getWriter().write(result);
		} catch (Exception e) {
			log.error("sevletZuulResponse fail", e);
		}
		return null;
	}

	@Override
	public Object sevletZuulResponseException(HttpStatus httpStatus,String msg) {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.setSendZuulResponse(false);
		HttpServletResponse httpServletResponse=ctx.getResponse();
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setStatus(httpStatus.value());
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> map = new HashMap<>();
        map.put("code",httpStatus.value());
        map.put("msg",StringUtils.isEmpty(msg)?httpStatus.getReasonPhrase():msg);  
		try {
			httpServletResponse.getWriter().write(serialization.serialize(map));
		} catch (Exception e) {
			log.error("sevletZuulResponse fail", e);
		}
		return null;
	}

}
