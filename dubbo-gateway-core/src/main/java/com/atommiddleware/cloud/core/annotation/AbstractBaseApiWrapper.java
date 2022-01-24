package com.atommiddleware.cloud.core.annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.dubbo.common.utils.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.serialize.Serialization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseApiWrapper implements BaseApiWrapper {

	protected Set<String> patterns = new HashSet<String>();

	@Autowired
	private Serialization serialization;
	@Autowired
	protected PathMatcher pathMatcher;

	@Override
	public Set<String> getPathPatterns() {
		return patterns;
	}

	protected void convertAttriToParam(ParamInfo paramInfo, Object obj, Object[] params) {
		if (paramInfo.isRequired() && null == obj) {
			throw new IllegalArgumentException("attribute Parameter verification exception");
		}
		params[paramInfo.getIndex()] = obj;
	}
	private static String inputConvertToString(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String str = "";
		StringBuilder wholeStr = new StringBuilder();
		while ((str = reader.readLine()) != null) {
			wholeStr.append(str);
		}
		return wholeStr.toString();
	}
	protected void convertBodyToParam(ParamInfo paramInfo, InputStream body, Object[] params) {
		if (paramInfo.isRequired() && StringUtils.isEmpty(body)) {
			throw new IllegalArgumentException("body Parameter verification exception");
		}
		final Map<String, Class<?>> mapClasses = DubboApiContext.MAP_CLASSES;
		Object param = null;
		if (null != body) {
			Class<?> paramTypeClass = mapClasses.get(paramInfo.getParamType());
			if (paramTypeClass.isPrimitive() || paramTypeClass == String.class) {
				String bodyString = null;
				try {
					bodyString = inputConvertToString(body);
				} catch (IOException e) {
					log.error("fail convertBodyToParam");
				}
				if (!StringUtils.isEmpty(bodyString)) {
					if (paramTypeClass.isPrimitive()) {
						param = ClassUtils.convertPrimitive(paramTypeClass, bodyString);
					} else {
						param = bodyString;
					}
				}
			} else {
				param = serialization.deserialize(body, paramTypeClass);
			}
		}
		if (paramInfo.isRequired() && null == param) {
			throw new IllegalArgumentException(
					"paramName:[" + paramInfo.getParamName() + "] Parameter verification exception");
		}
		params[paramInfo.getIndex()] = param;
	}

	protected void convertParam(List<ParamInfo> listParams, Map<String, String> mapPathParams, Object[] params) {
		String paramValue = null;
		Object param = null;
		final Map<String, Class<?>> mapClasses = DubboApiContext.MAP_CLASSES;
		for (ParamInfo paramInfo : listParams) {
			param = null;
			paramValue = mapPathParams.get(paramInfo.getParamName());
			if (paramInfo.isRequired() && null == paramValue) {
				throw new IllegalArgumentException(
						"paramName:[" + paramInfo.getParamName() + "] Parameter verification exception");
			}
			if (!StringUtils.isEmpty(paramValue)) {
				Class<?> paramTypeClass = mapClasses.get(paramInfo.getParamType());
				if (paramTypeClass.isPrimitive()) {
					param = ClassUtils.convertPrimitive(paramTypeClass, paramValue);
				} else {
					if (paramTypeClass == String.class) {
						param = paramValue;
					} else {
						try {
							paramValue = java.net.URLDecoder.decode(paramValue, DubboApiContext.CHARSET);
						} catch (UnsupportedEncodingException e) {
							log.error("decode fail", e);
						}
						param = serialization.deserialize(paramValue, paramTypeClass);
					}
				}
			}
			params[paramInfo.getIndex()] = param;
		}
		mapPathParams.clear();
	}
}
