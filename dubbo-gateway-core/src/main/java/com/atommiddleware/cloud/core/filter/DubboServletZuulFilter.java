package com.atommiddleware.cloud.core.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.RIBBON_ROUTING_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_HEADER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.core.annotation.DubboApiServletWrapper;
import com.atommiddleware.cloud.core.annotation.ResponseZuulServletResult;
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
	private final String APPLICATION_FORM_URLENCODED_UTF8_VALUE = MediaType.APPLICATION_FORM_URLENCODED_VALUE
			+ ";charset=UTF-8";

	public DubboServletZuulFilter(PathMatcher pathMatcher, Serialization serialization, ResponseZuulServletResult responseResult) {
		this.pathMatcher = pathMatcher;
		this.serialization = serialization;
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

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest httpServletRequest = ctx.getRequest();
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
			return responseResult.sevletZuulResponseException(HttpStatus.NOT_FOUND,null);
		} else {
			final String pathPattern = pathPatternTemp;
			RequestMethod requestMethod = DubboApiContext.PATTERNS_REQUESTMETHOD.get(pathPattern);
			String httpMethodName = httpServletRequest.getMethod();
			if (!httpMethodName.equals(requestMethod.name())) {
				log.error("path:{} requestMethod is fail PathMapping requestMethod:{}", pathPattern,
						requestMethod.name());
				return responseResult.sevletZuulResponseException(HttpStatus.METHOD_NOT_ALLOWED,String.format(" PathMapping suported requestMethod:%s", requestMethod.name()));
			} else {
				final DubboApiServletWrapper dubboApiWrapper = dubboApiWrapperTemp;
				// 只接收get 和post 请求 减少复杂性
				if (httpMethodName.equals(RequestMethod.POST.name())) {
					String contentType=httpServletRequest.getContentType();
					if(StringUtils.isEmpty(contentType)) {
						log.error("path:[{}] body param media must application/json or application/x-www-form-urlencoded", pathPattern);
						return responseResult.sevletZuulResponseException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,null);
					}
					if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)
							|| contentType.equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
						// 获取body 执行
						try {
							CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
									HttpUtils.getBodyParam(httpServletRequest));
							return responseResult.sevletZuulResponse(serialization.serialize(completableFuture.get()));
						}
						catch(ResponseStatusException e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
							return responseResult.sevletZuulResponseException(e.getStatus(), e.getReason());
						}
						catch (Exception e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
						}
						return responseResult.sevletZuulResponseException(HttpStatus.INTERNAL_SERVER_ERROR,"dubboApiWrapper.handler fail");
					}
					else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
							|| contentType.equals(APPLICATION_FORM_URLENCODED_UTF8_VALUE)) {
						try {
						CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
								httpServletRequest.getParameterMap());
							return responseResult.sevletZuulResponse(serialization.serialize(completableFuture.get()));
						} 
						catch(ResponseStatusException e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
							return responseResult.sevletZuulResponseException(e.getStatus(),e.getReason());
						}
						catch (Exception e) {
							log.error("path:[" + pathPattern + "] fail to apply ", e);
						}
						return responseResult.sevletZuulResponseException(HttpStatus.INTERNAL_SERVER_ERROR,"dubboApiWrapper.handler fail");
					} else {
						log.error("path:[{}] body param media must application/json", pathPattern);
						return responseResult.sevletZuulResponseException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,null);
					}
				} else if (httpMethodName.equals(RequestMethod.GET.name())) {
					try {
					CompletableFuture completableFuture = dubboApiWrapper.handler(pathPattern, httpServletRequest,
							null);
						return responseResult.sevletZuulResponse(serialization.serialize(completableFuture.get()));
					} 
					catch(ResponseStatusException e) {
						log.error("path:[" + pathPattern + "] fail to apply ", e);
						return responseResult.sevletZuulResponseException(e.getStatus(),e.getReason());
					}
					catch (Exception e) {
						log.error("path:[" + pathPattern + "] fail to apply ", e);
					}
					return responseResult.sevletZuulResponseException(HttpStatus.INTERNAL_SERVER_ERROR,"dubboApiWrapper.handler fail");
				} else {
					log.error("Only get and post are supported for the time being ", pathPattern, requestMethod.name());
					return responseResult.sevletZuulResponseException(HttpStatus.METHOD_NOT_ALLOWED,null);
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
