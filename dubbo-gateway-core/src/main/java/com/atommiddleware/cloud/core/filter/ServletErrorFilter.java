package com.atommiddleware.cloud.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.atommiddleware.cloud.core.annotation.ResponseServletResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServletErrorFilter implements Filter {

	private final ResponseServletResult responseResult;

	public ServletErrorFilter(ResponseServletResult responseResult) {
		this.responseResult = responseResult;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (ResponseStatusException e) {
			log.error(" fail to apply ", e);
			responseResult.sevletResponseException((HttpServletRequest) request, (HttpServletResponse) response,
					e.getStatus(), e.getReason());
			return;
		} catch (Exception e) {
			log.error("fail to apply ", e);
			responseResult.sevletResponseException((HttpServletRequest) request, (HttpServletResponse) response,
					HttpStatus.INTERNAL_SERVER_ERROR, null);
		}

	}

}
