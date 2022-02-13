package com.atommiddleware.cloud.core.annotation;

import java.io.UnsupportedEncodingException;
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

import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFromType;
import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public abstract class AbstractDubboApiServletWrapper extends AbstractBaseApiWrapper implements DubboApiServletWrapper{

	@Override
	public CompletableFuture handler(String pathPattern, HttpServletRequest httpServletRequest, Object body) {
		  throw new UnsupportedOperationException();
	}
	
	private String decodeUrlEncode(String value) {
		if (!StringUtils.isEmpty(value)) {
			try {
				value=java.net.URLDecoder.decode(value, DubboApiContext.CHARSET);
			} catch (UnsupportedEncodingException e) {
				log.error("decode fail", e);
			}
		}
		return value;
	}
	protected void handlerConvertParams(String pathPattern, HttpServletRequest httpServletRequest, Object[] params, Object body) throws InterruptedException, ExecutionException, IllegalAccessException, InvocationTargetException, InstantiationException {
		final Map<ParamFromType, List<ParamInfo>> mapGroupByParamType = DubboApiContext.MAP_PARAM_INFO.get(pathPattern);
		final Map<String, String> mapPathParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		// cookie
		List<ParamInfo> listParams = mapGroupByParamType.get(ParamFromType.FROM_COOKIE);
		if (!CollectionUtils.isEmpty(listParams)) {
			Cookie[] cookies = httpServletRequest.getCookies();
			Arrays.stream(cookies).forEach(o -> {
				mapPathParams.put(o.getName(), decodeUrlEncode(o.getValue()));
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
			Enumeration<String> enumHeaderNames = httpServletRequest.getHeaderNames();
			String headerValue=null;
			String headerName=null;
		      while (enumHeaderNames.hasMoreElements()){
		    	  headerName=enumHeaderNames.nextElement();
		    	  headerValue = httpServletRequest.getHeader(headerName);
		    	  if (!StringUtils.isEmpty(headerValue)) {
		    		mapPathParams.put(headerName,decodeUrlEncode(headerValue));
		    	  }
		      }
			convertParam(listParams, mapPathParams, params);
		}

		// path
		listParams = mapGroupByParamType.get(ParamFromType.FROM_PATH);
		if (!CollectionUtils.isEmpty(listParams)) {
			pathMatcher.extractUriTemplateVariables(pathPattern, httpServletRequest.getRequestURI()).forEach((key,value)->{
				mapPathParams.put(key, decodeUrlEncode(value));
			});
			convertParam(listParams, mapPathParams, params);
		}
		// queryParams
		listParams = mapGroupByParamType.get(ParamFromType.FROM_QUERYPARAMS);
		if (!CollectionUtils.isEmpty(listParams)) {
			mapPathParams.putAll(HttpUtils.getUrlParams(httpServletRequest, DubboApiContext.CHARSET));
			convertParam(listParams, mapPathParams, params);
		}
		// from attribute
		listParams = mapGroupByParamType.get(ParamFromType.FROM_ATTRIBUTE);
		if (!CollectionUtils.isEmpty(listParams)) {
			listParams.forEach(o -> {
				convertAttriToParam(o, httpServletRequest.getAttribute(o.getParamName()), params);
			});
		}
	}
}
