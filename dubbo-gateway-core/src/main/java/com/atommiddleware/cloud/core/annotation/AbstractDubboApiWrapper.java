package com.atommiddleware.cloud.core.annotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import com.atommiddleware.cloud.core.context.DubboApiContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDubboApiWrapper extends AbstractBaseApiWrapper implements DubboApiWrapper {

	@Override
	public CompletableFuture handler(String pathPattern, ServerWebExchange exchange, String body) {
		throw new UnsupportedOperationException();
	}
	protected void handlerConvertParams(String pathPattern, ServerWebExchange exchange, Object[] params, String body)
			throws InterruptedException, ExecutionException {
		ServerHttpRequest serverHttpRequest = exchange.getRequest();
		final Map<Integer, List<ParamInfo>> mapGroupByParamType = DubboApiContext.MAP_PARAM_INFO.get(pathPattern);
		final Map<String, String> mapPathParams = new HashMap<String, String>();
		// cookie
		List<ParamInfo> listParams = mapGroupByParamType.get(ParamFromType.FROM_COOKIE.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			MultiValueMap<String, HttpCookie> cookies = serverHttpRequest.getCookies();
			listParams.forEach(o -> {
				HttpCookie httpCookie = cookies.getFirst(o.getParamName());
				if (null != httpCookie) {
					mapPathParams.put(o.getParamName(), httpCookie.getValue());
				}
			});
			convertParam(listParams, mapPathParams, params);
		}
		// body
		listParams = mapGroupByParamType.get(ParamFromType.FROM_BODY.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			if (listParams.size() > 1) {
				throw new IllegalArgumentException("body Parameter verification exception");
			}
			convertBodyToParam(listParams.get(0), body, params);
		}
		// header
		listParams = mapGroupByParamType.get(ParamFromType.FROM_HEADER.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
			listParams.forEach(o -> {
				String headerValue = httpHeaders.getFirst(o.getParamName());
				if (!StringUtils.isEmpty(headerValue)) {
					mapPathParams.put(o.getParamName(), headerValue);
				}
			});
			convertParam(listParams, mapPathParams, params);
		}
		// path
		listParams = mapGroupByParamType.get(ParamFromType.FROM_PATH.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams
					.putAll(pathMatcher.extractUriTemplateVariables(pathPattern, serverHttpRequest.getPath().value()));
			convertParam(listParams, mapPathParams, params);
		}
		// queryParams
		listParams = mapGroupByParamType.get(ParamFromType.FROM_QUERYPARAMS.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
			listParams.forEach(o -> {
				String queryParam = queryParams.getFirst(o.getParamName());
				if (!StringUtils.isEmpty(queryParam)) {
					mapPathParams.put(o.getParamName(), queryParam);
				}
			});
			convertParam(listParams, mapPathParams, params);
		}
		// from attribute
		listParams = mapGroupByParamType.get(ParamFromType.FROM_ATTRIBUTE.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			listParams.forEach(o -> {
				convertAttriToParam(o, exchange.getAttribute(o.getParamName()), params);
			});
		}
	}

}
