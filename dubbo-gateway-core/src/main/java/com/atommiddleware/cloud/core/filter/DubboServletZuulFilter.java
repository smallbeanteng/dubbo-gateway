package com.atommiddleware.cloud.core.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.RIBBON_ROUTING_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_HEADER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.core.annotation.DubboApiServletWrapper;
import com.atommiddleware.cloud.core.annotation.ResponseZuulServletResult;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.core.utils.HttpUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DubboServletZuulFilter extends ZuulFilter {

	private final PathMatcher pathMatcher;
	private final Serialization serialization;
	private final ResponseZuulServletResult responseResult;
	private final int order;
	private final String APPLICATION_FORM_URLENCODED_UTF8_VALUE = MediaType.APPLICATION_FORM_URLENCODED_VALUE
			+ ";charset=UTF-8";

	public DubboServletZuulFilter(PathMatcher pathMatcher, Serialization serialization,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, ResponseZuulServletResult responseResult) {
		this.pathMatcher = pathMatcher;
		this.serialization = serialization;
		this.order = dubboReferenceConfigProperties.getFilterOrder();
		this.responseResult = responseResult;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		Object objServiceId = ctx.get(SERVICE_ID_KEY);
		if ((ctx.getRouteHost() == null && null != objServiceId && ctx.sendZuulResponse()
				&& String.valueOf(objServiceId).startsWith("dubbo:"))) {
			ctx.remove(SERVICE_ID_KEY);
			ctx.remove(SERVICE_ID_HEADER);
			return true;
		}
		return false;

	}

	private Object response500(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		return responseResult.sevletZuulResponse(null, true);
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest httpServletRequest = ctx.getRequest();
		HttpServletResponse httpServletResponse = ctx.getResponse();
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
			log.error("not find dubbo service for path:[{}]", pathPatternTemp);
			return response500(httpServletRequest, httpServletResponse);
		} else {
			final String pathPattern = pathPatternTemp;
			RequestMethod requestMethod = DubboApiContext.PATTERNS_REQUESTMETHOD.get(pathPattern);
			String httpMethodName = httpServletRequest.getMethod();
			if (!httpMethodName.equals(requestMethod.name())) {
				log.error("path:{} requestMethod is fail PathMapping requestMethod:{}", pathPattern,
						requestMethod.name());
				return response500(httpServletRequest, httpServletResponse);
			} else {
				final DubboApiServletWrapper dubboApiWrapper = dubboApiWrapperTemp;
				// 只接收get 和post 请求 减少复杂性
				if (httpMethodName.equals(RequestMethod.POST.name())) {
					if (httpServletRequest.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)
							|| httpServletRequest.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
						// 获取body 执行
						CompletableFuture completableFuture;
						try {
							completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
									HttpUtils.getBodyParam(httpServletRequest));
						} catch (IOException e1) {
							log.error("dubboApiWrapper.handler fail",e1);
							return response500(httpServletRequest, httpServletResponse);
						}
						try {
							return responseResult.sevletZuulResponse(serialization.serialize(completableFuture.get()),
									false);
						} catch (InterruptedException | ExecutionException e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
						}
						return response500(httpServletRequest, httpServletResponse);
					}
					// 还不成熟暂不支持
					else if (httpServletRequest.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
							|| httpServletRequest.getContentType().equals(APPLICATION_FORM_URLENCODED_UTF8_VALUE)) {
						CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
								httpServletRequest.getParameterMap());
						try {
							return responseResult.sevletZuulResponse(serialization.serialize(completableFuture.get()),
									false);
						} catch (InterruptedException | ExecutionException e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
						}
						return response500(httpServletRequest, httpServletResponse);
					} else {
						log.error("path:[{}] body param media must application/json", pathPattern);
						return response500(httpServletRequest, httpServletResponse);
					}
				} else if (httpMethodName.equals(RequestMethod.GET.name())) {
					CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
							null);
					try {
						return responseResult.sevletZuulResponse(serialization.serialize(completableFuture.get()),
								false);
					} catch (InterruptedException | ExecutionException e) {
						log.error("path:[" + pathPattern + "] fail to apply ", e);
					}
					return response500(httpServletRequest, httpServletResponse);
				} else {
					log.error("Only get and post are supported for the time being ", pathPattern, requestMethod.name());
					return response500(httpServletRequest, httpServletResponse);
				}

			}
		}
	}

	@Override
	public String filterType() {
		return FilterConstants.ROUTE_TYPE;
	}

	@Override
	public int filterOrder() {
		return RIBBON_ROUTING_FILTER_ORDER - 10;
	}
}
