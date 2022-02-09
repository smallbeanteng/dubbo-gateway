package com.atommiddleware.cloud.core.annotation;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.dubbo.common.utils.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFormat;
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

	protected void convertBodyToParam(ParamInfo paramInfo, Object body, Object[] params)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if (paramInfo.isRequired() && StringUtils.isEmpty(body)) {
			throw new IllegalArgumentException("body Parameter verification exception");
		}
		final Map<String, Class<?>> mapClasses = DubboApiContext.MAP_CLASSES;
		Object param = null;
		if (null != body) {
			Class<?> paramTypeClass = mapClasses.get(paramInfo.getParamType());
			if (paramInfo.isSimpleType()) {
				String bodyString = null;
				if (body instanceof String) {
					bodyString = (String) body;
				} else {
					if (body instanceof MultiValueMap) {
						MultiValueMap<String, String> multiValueMap = (MultiValueMap<String, String>) body;
						bodyString = multiValueMap.getFirst(paramInfo.getParamName());
						multiValueMap.clear();
					} else {
						Map<String, String[]> mapValue = (Map<String, String[]>) body;
						String[] strValues = mapValue.get(paramInfo.getParamName());
						if (null != strValues && strValues.length > 0) {
							bodyString = strValues[0];
						}
						mapValue.clear();
					}
				}
				if (!StringUtils.isEmpty(bodyString)) {
					param = ClassUtils.convertPrimitive(paramTypeClass, bodyString);
				}
			} else {
				if (body instanceof String) {
					param = serialization.deserialize((String) body, paramTypeClass);
				} else {
					if (body instanceof MultiValueMap) {
						MultiValueMap<String, String> multiValueMap = (MultiValueMap<String, String>) body;
						param = serialization.convertValue(multiValueMap.toSingleValueMap(), paramTypeClass);
						multiValueMap.clear();
					} else {
						Map<String, String[]> multiValueMap = (Map<String, String[]>) body;
						Map<String, String> mapValues = new HashMap<String, String>();
						multiValueMap.forEach((key, v) -> {
							if (null != v && v.length > 0) {
								mapValues.put(key, v[0]);
							}
						});
						param = serialization.convertValue(mapValues, paramTypeClass);
						mapValues.clear();
					}
				}
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
		Class<?> paramTypeClass;
		for (ParamInfo paramInfo : listParams) {
			param = null;
			paramTypeClass = mapClasses.get(paramInfo.getParamType());
			if (ClassUtils.isSimpleType(paramTypeClass)) {
				paramValue = mapPathParams.get(paramInfo.getParamName());
				if (!StringUtils.isEmpty(paramValue)) {
				param = ClassUtils.convertPrimitive(paramTypeClass, paramValue);
				}
			}else {
				if(paramInfo.getParamFormat()==ParamFormat.MAP.ordinal()) {
					param= serialization.convertValue(mapPathParams, paramTypeClass);
				}else {
					paramValue = mapPathParams.get(paramInfo.getParamName());
					try {
						paramValue = java.net.URLDecoder.decode(paramValue, DubboApiContext.CHARSET);
					} catch (UnsupportedEncodingException e) {
						log.error("decode fail", e);
					}
					param = serialization.deserialize(paramValue, paramTypeClass);
				}
			}
			if (paramInfo.isRequired() && null == param) {
				throw new IllegalArgumentException(
						"paramName:[" + paramInfo.getParamName() + "] Parameter verification exception");
			}
			params[paramInfo.getIndex()] = param;
		}
		mapPathParams.clear();
	}
}
