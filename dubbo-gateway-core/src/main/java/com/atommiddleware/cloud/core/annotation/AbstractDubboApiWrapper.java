package com.atommiddleware.cloud.core.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFromType;
import com.atommiddleware.cloud.core.context.DubboApiContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDubboApiWrapper extends AbstractBaseApiWrapper implements DubboApiWrapper {

	@Override
	public CompletableFuture handler(String pathPattern, ServerWebExchange exchange,Object body) {
		throw new UnsupportedOperationException();
	}
	protected void handlerConvertParams(String pathPattern, ServerWebExchange exchange, Object[] params, Object body)
			throws InterruptedException, ExecutionException, IllegalAccessException, InvocationTargetException, InstantiationException {
		ServerHttpRequest serverHttpRequest = exchange.getRequest();
		final Map<ParamFromType, List<ParamInfo>> mapGroupByParamType = DubboApiContext.MAP_PARAM_INFO.get(pathPattern);
		final Map<String, String> mapPathParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		// cookie
		List<ParamInfo> listParams = mapGroupByParamType.get(ParamFromType.FROM_COOKIE);
		if (!CollectionUtils.isEmpty(listParams)) {
			serverHttpRequest.getCookies().forEach((key, values) -> {
				if (values != null && !values.isEmpty()) {
					mapPathParams.put(key, values.get(0).getValue());
				}
			});
			convertParam(listParams, mapPathParams, params);
		}
		
		// body
		listParams = mapGroupByParamType.get(ParamFromType.FROM_BODY);
		if (!CollectionUtils.isEmpty(listParams)) {
			if (listParams.size() > 1) {
				throw new IllegalArgumentException("body Parameter verification exception");
			}
			convertBodyToParam(listParams.get(0), body, params);
		}
		// header
		listParams = mapGroupByParamType.get(ParamFromType.FROM_HEADER);
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams.putAll(serverHttpRequest.getHeaders().toSingleValueMap());
			convertParam(listParams,mapPathParams, params);
		}
		// path
		listParams = mapGroupByParamType.get(ParamFromType.FROM_PATH);
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams
					.putAll(pathMatcher.extractUriTemplateVariables(pathPattern, serverHttpRequest.getPath().value()));
			convertParam(listParams, mapPathParams, params);
		}
		
		// queryParams
		listParams = mapGroupByParamType.get(ParamFromType.FROM_QUERYPARAMS);
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams.putAll(serverHttpRequest.getQueryParams().toSingleValueMap());
			convertParam(listParams,mapPathParams, params);
		}
		// from attribute
		listParams = mapGroupByParamType.get(ParamFromType.FROM_ATTRIBUTE);
		if (!CollectionUtils.isEmpty(listParams)) {
			listParams.forEach(o -> {
				convertAttriToParam(o, exchange.getAttribute(o.getParamName()), params);
			});
		}
	}

}
