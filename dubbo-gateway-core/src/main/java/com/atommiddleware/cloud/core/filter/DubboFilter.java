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
import org.springframework.http.MediaType;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.core.annotation.DubboApiServletWrapper;
import com.atommiddleware.cloud.core.annotation.ResponseServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.serialize.Serialization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DubboFilter implements Filter, OrderedFilter {

	private final PathMatcher pathMatcher;
	private final Serialization serialization;
	private final ResponseServletResult responseResult;
	private final int order;

	public DubboFilter(PathMatcher pathMatcher, Serialization serialization,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, ResponseServletResult responseResult) {
		this.pathMatcher = pathMatcher;
		this.serialization = serialization;
		this.order = dubboReferenceConfigProperties.getFilterOrder();
		this.responseResult = responseResult;
	}

	private void response500(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		responseResult.sevletResponse(httpServletRequest, httpServletResponse, null, true);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String path = httpServletRequest.getRequestURI();
		String pathPatternTemp = path;
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
		if (null == dubboApiWrapperTemp) {
			chain.doFilter(request, response);
			return;
		} else {
			final String pathPattern = pathPatternTemp;
			RequestMethod requestMethod = DubboApiContext.PATTERNS_REQUESTMETHOD.get(pathPattern);
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			String httpMethodName = httpServletRequest.getMethod();
			if (!httpMethodName.equals(requestMethod.name())) {
				log.error("path:{} requestMethod is fail PathMapping requestMethod:{}", pathPattern,
						requestMethod.name());
				response500(httpServletRequest, httpServletResponse);
				return;
			} else {
				final DubboApiServletWrapper dubboApiWrapper = dubboApiWrapperTemp;
				// 只接收get 和post 请求 减少复杂性
				if (httpMethodName.equals(RequestMethod.POST.name())) {
					if (!httpServletRequest.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
						log.error("path:{} body param media must application/json", pathPattern);
						response500(httpServletRequest, httpServletResponse);
						return;
					}
					// 获取body 执行
					//String bodyString = HttpUtils.getBodyParam(httpServletRequest);
					CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
							httpServletRequest.getInputStream());
					try {
						responseResult.sevletResponse(httpServletRequest, httpServletResponse,
								serialization.serialize(completableFuture.get()), false);
						return;
					} catch (InterruptedException | ExecutionException e) {
						log.error("path:[" + pathPattern + "] fail to apply ", e);
					}
					response500(httpServletRequest, httpServletResponse);
					return;
				} else if (httpMethodName.equals(RequestMethod.GET.name())) {
					CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
							null);
					try {
						responseResult.sevletResponse(httpServletRequest, httpServletResponse,
								serialization.serialize(completableFuture.get()), false);
						return;
					} catch (InterruptedException | ExecutionException e) {
						log.error("path:[" + pathPattern + "] fail to apply ", e);
					}
					response500(httpServletRequest, httpServletResponse);
					return;
				} else {
					log.error("Only get and post are supported for the time being ", pathPattern, requestMethod.name());
					response500(httpServletRequest, httpServletResponse);
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
