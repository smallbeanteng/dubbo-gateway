package com.atommiddleware.cloud.core.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.core.annotation.DubboApiServletWrapper;
import com.atommiddleware.cloud.core.annotation.ResponseServletResult;
import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.core.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DubboServletFilter implements Filter, OrderedFilter {

	private final PathMatcher pathMatcher;
	private final Serialization serialization;
	private final ResponseServletResult responseResult;
	private final int order;
	private final String APPLICATION_FORM_URLENCODED_UTF8_VALUE = MediaType.APPLICATION_FORM_URLENCODED_VALUE
			+ ";charset=UTF-8";

	private final String[] excludUrlPatterns;
	public DubboServletFilter(PathMatcher pathMatcher, Serialization serialization,
			int order, ResponseServletResult responseResult,String[] excludUrlPatterns) {
		this.pathMatcher = pathMatcher;
		this.serialization = serialization;
		this.order = order;
		this.responseResult = responseResult;
		this.excludUrlPatterns=excludUrlPatterns;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String path = httpServletRequest.getRequestURI();
		String pathPatternTemp = path;
		//排除匹配
		if(null!=excludUrlPatterns&&excludUrlPatterns.length>0) {
			boolean isExclud=false;
			for(String urlPattern:excludUrlPatterns) {
				if(pathMatcher.match(urlPattern, path)) {
					isExclud=true;
					break;
				}
			}
			if(isExclud) {
				chain.doFilter(request, response);
				return;
			}
		}
		DubboApiServletWrapper dubboApiWrapperTemp = DubboApiContext.MAP_DUBBO_API_SERVLET_WRAPPER.get(path);
		if (null == dubboApiWrapperTemp) {
			for (Map.Entry<String, DubboApiServletWrapper> entry : DubboApiContext.MAP_DUBBO_API_PATH_PATTERN_SERVLET_WRAPPER
					.entrySet()) {
				if (pathMatcher.match(entry.getKey(), path)) {
					dubboApiWrapperTemp = entry.getValue();
					pathPatternTemp = entry.getKey();
					break;
				}
			}
		}
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (null == dubboApiWrapperTemp) {
			log.error("not find dubbo service for path:[{}]", pathPatternTemp);
			responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.NOT_FOUND, null);
			return;
		} else {
			final String pathPattern = pathPatternTemp;
			RequestMethod requestMethod = DubboApiContext.PATTERNS_REQUESTMETHOD.get(pathPattern);
			String httpMethodName = httpServletRequest.getMethod();
			if (!httpMethodName.equals(requestMethod.name())) {
				log.error("path:{} requestMethod is fail PathMapping requestMethod:{}", pathPattern,
						requestMethod.name());
				responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.METHOD_NOT_ALLOWED, null);
				return;
			} else {
				final DubboApiServletWrapper dubboApiWrapper = dubboApiWrapperTemp;
				// 只接收get 和post 请求 减少复杂性
				if (httpMethodName.equals(RequestMethod.POST.name())) {
					String contentType=httpServletRequest.getContentType();
					if(StringUtils.isEmpty(contentType)) {
						log.error("path:[{}] body param media must application/json or application/x-www-form-urlencoded", pathPattern);
						responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE,null);
						return;
					}
					if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)
							|| contentType.equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
						// 获取body 执行
						try {
						CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
								HttpUtils.getBodyParam(httpServletRequest));
							responseResult.sevletResponse(httpServletRequest, httpServletResponse,
									serialization.serialize(completableFuture.get()));
							return;
						} catch (Exception e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
						}
						responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.INTERNAL_SERVER_ERROR, "dubboApiWrapper.handler fail");
						return;
					}
					else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
							|| contentType.equals(APPLICATION_FORM_URLENCODED_UTF8_VALUE)) {
						try {
						CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
								httpServletRequest.getParameterMap());
							responseResult.sevletResponse(httpServletRequest, httpServletResponse,
									serialization.serialize(completableFuture.get()));
							return;
						} catch (InterruptedException | ExecutionException e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
						}
						responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.INTERNAL_SERVER_ERROR, "dubboApiWrapper.handler fail");
						return;
					} else {
						log.error("path:[{}] body param media must application/json or application/x-www-form-urlencoded", pathPattern);
						responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE,null);
						return;
					}
				} else if (httpMethodName.equals(RequestMethod.GET.name())) {
					try {
					CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
							null);
						responseResult.sevletResponse(httpServletRequest, httpServletResponse,
								serialization.serialize(completableFuture.get()));
						return;
					} catch (Exception e) {
						log.error("path:[" + pathPattern + "] fail to apply ", e);
					}
					responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.INTERNAL_SERVER_ERROR, "dubboApiWrapper.handler fail");
					return;
				} else {
					log.error("Only get and post are supported for the time being ", pathPattern, requestMethod.name());
					responseResult.sevletResponseException(httpServletRequest, httpServletResponse, HttpStatus.METHOD_NOT_ALLOWED, null);
					return;
				}

			}
		}
	}

	@Override
	public int getOrder() {
		return this.order;
	}

}
