package com.atommiddleware.cloud.core.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebInputException;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.core.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ForwardingServiceController {

	@Autowired
	private DubboReferenceConfigProperties dubboReferenceConfigProperties;
	@Autowired
	private Serialization serialization;

	@RequestMapping(value = "/cas/redirect", method = { RequestMethod.POST, RequestMethod.GET })
	public void redirect(@RequestParam("service") String service, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) {
		if (HttpUtils.isAjax(httpRequest)) {
			httpServletResponse.setCharacterEncoding(dubboReferenceConfigProperties.getCharset());
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			Map<String, Object> map = new HashMap<>();
			map.put("code", 302);
			map.put("location", service);
			try {
				PrintWriter writer = httpServletResponse.getWriter();
				writer.write(serialization.serialize(map, true));
			} catch (IOException e) {
				log.error("outData error", e);
			}
		} else {
			try {
				httpServletResponse.sendRedirect(service);
			} catch (IOException e) {
				throw new ServerWebInputException("fail redirect service");
			}
		}
	}
}
