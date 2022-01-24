package com.atommiddleware.cloud.core.annotation;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultResponseServletResult implements ResponseServletResult{

	private final String ERRORRESULT="{\"code\": 500}";
	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;
	public DefaultResponseServletResult(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
		this.dubboReferenceConfigProperties=dubboReferenceConfigProperties;
	}
	@Override
	public void sevletResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String result, boolean isErrorResponse) {
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
            PrintWriter writer = httpServletResponse.getWriter();
            writer.print(result);
            httpServletResponse.flushBuffer();
        } catch (IOException e) {
            log.error("outData error------------------:", e);
        }
	}
}
