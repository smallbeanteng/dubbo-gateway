package com.atommiddleware.cloud.core.annotation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
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
		HttpServletRequest httpServletRequest=ctx.getRequest();
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		httpServletResponse.setHeader("Access-Control-Allow-Origin",
				httpServletRequest.getHeader("Access-Control-Allow-Origin"));
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType("application/json");
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
		HttpServletRequest httpServletRequest=ctx.getRequest();
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		httpServletResponse.setHeader("Access-Control-Allow-Origin",
				httpServletRequest.getHeader("Access-Control-Allow-Origin"));
		
		httpServletResponse.setStatus(httpStatus.value());
		httpServletResponse.setContentType("application/json");
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
