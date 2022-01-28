package com.atommiddleware.cloud.core.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultResponseZuulServletResult implements ResponseZuulServletResult {

	private final String ERRORRESULT = "{\"code\": 500}";
	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;

	public DefaultResponseZuulServletResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		this.dubboReferenceConfigProperties = dubboReferenceConfigProperties;
	}

	@Override
	public Object sevletZuulResponse(String result, boolean isErrorResponse) {
		RequestContext ctx = RequestContext.getCurrentContext();
		// zuul 网关直接返回响应，不让请求访问后续的接口
		ctx.setSendZuulResponse(false);
		ctx.setResponseStatusCode(500);
		HttpServletResponse httpServletResponse=ctx.getResponse();
		HttpServletRequest httpServletRequest=ctx.getRequest();
		
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		httpServletResponse.setHeader("Access-Control-Allow-Origin",
				httpServletRequest.getHeader("Access-Control-Allow-Origin"));
		if (isErrorResponse) {
			result=ERRORRESULT;
			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else {
			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		}
		httpServletResponse.setContentType("application/json");
		try {
			httpServletResponse.getWriter().write(result);
		} catch (Exception e) {
			log.error("sevletZuulResponse fail", e);
		}
		return null;
	}

}
