package com.atommiddleware.cloud.core.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.utils.HttpUtils;

public abstract class AbstractDubboApiServletWrapper extends AbstractBaseApiWrapper implements DubboApiServletWrapper{

	@Override
	public CompletableFuture handler(String pathPattern, HttpServletRequest httpServletRequest, Object body) {
		  throw new UnsupportedOperationException();
	}
	
	protected void handlerConvertParams(String pathPattern, HttpServletRequest httpServletRequest, Object[] params, Object body) throws InterruptedException, ExecutionException, IllegalAccessException, InvocationTargetException, InstantiationException {
		final Map<Integer, List<ParamInfo>> mapGroupByParamType = DubboApiContext.MAP_PARAM_INFO.get(pathPattern);
		final Map<String, String> mapPathParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		// cookie
		List<ParamInfo> listParams = mapGroupByParamType.get(ParamFromType.FROM_COOKIE.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			Cookie[] cookies = httpServletRequest.getCookies();
			Arrays.stream(cookies).forEach(o -> {
				mapPathParams.put(o.getName(), o.getValue());
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
			Enumeration<String> enumHeaderNames = httpServletRequest.getHeaderNames();
			String headerValue=null;
			String headerName=null;
		      while (enumHeaderNames.hasMoreElements()){
		    	  headerName=enumHeaderNames.nextElement();
		    	  headerValue = httpServletRequest.getHeader(headerName);
		    	  if (!StringUtils.isEmpty(headerValue)) {
		    		mapPathParams.put(headerName.toLowerCase(),headerValue);
		    	  }
		      }
			convertParam(listParams, mapPathParams, params);
		}

		// path
		listParams = mapGroupByParamType.get(ParamFromType.FROM_PATH.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams
					.putAll(pathMatcher.extractUriTemplateVariables(pathPattern, httpServletRequest.getRequestURI()));
			convertParam(listParams, mapPathParams, params);
		}
		// queryParams
		listParams = mapGroupByParamType.get(ParamFromType.FROM_QUERYPARAMS.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams.putAll(HttpUtils.getUrlParams(httpServletRequest, DubboApiContext.CHARSET));
			convertParam(listParams, mapPathParams, params);
		}
		// from attribute
		listParams = mapGroupByParamType.get(ParamFromType.FROM_ATTRIBUTE.getParamFromType());
		if (!CollectionUtils.isEmpty(listParams)) {
			listParams.forEach(o -> {
				convertAttriToParam(o, httpServletRequest.getAttribute(o.getParamName()), params);
			});
		}
	}
}
