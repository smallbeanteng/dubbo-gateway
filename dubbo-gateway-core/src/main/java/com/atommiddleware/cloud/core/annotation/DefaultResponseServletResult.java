package com.atommiddleware.cloud.core.annotation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.serialize.Serialization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultResponseServletResult implements ResponseServletResult{

	private final DubboReferenceConfigProperties dubboReferenceConfigProperties;
    private final Serialization serialization;
	public DefaultResponseServletResult(DubboReferenceConfigProperties dubboReferenceConfigProperties,Serialization serialization) {
		this.dubboReferenceConfigProperties=dubboReferenceConfigProperties;
		this.serialization=serialization;
	}
	@Override
	public void sevletResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String result) {
		httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
		httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		httpServletResponse.setHeader("Access-Control-Allow-Origin",
				httpServletRequest.getHeader("Access-Control-Allow-Origin"));
			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType("application/json");
		try {
            PrintWriter writer = httpServletResponse.getWriter();
            if(StringUtils.isEmpty(result)) {
            	result="";
            }
            writer.write(result);
        } catch (IOException e) {
            log.error("outData error", e);
        }
	}

	@Override
	public void sevletResponseException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			HttpStatus httpStatus, String msg) {
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
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write(serialization.serialize(map));
        } catch (IOException e) {
            log.error("outData error", e);
        }
	}
}
