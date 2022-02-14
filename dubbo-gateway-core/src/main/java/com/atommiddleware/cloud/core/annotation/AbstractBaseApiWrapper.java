package com.atommiddleware.cloud.core.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.dubbo.common.utils.ClassUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFormat;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.context.DubboApiContext;
import com.atommiddleware.cloud.core.security.XssSecurity;
import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterStrategy;
import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.security.validation.ParamValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseApiWrapper implements BaseApiWrapper, InitializingBean {

	protected Set<String> patterns = new HashSet<String>();

	@Autowired
	private Serialization serialization;
	@Autowired
	protected PathMatcher pathMatcher;
	@Autowired
	private DubboReferenceConfigProperties dubboReferenceConfigProperties;
	@Autowired(required = false)
	private XssSecurity xssSecurity;
	@Autowired(required = false)
	private ParamValidator paramValidator;
	
	private boolean xssFilterEnable = true;
	private boolean validateParamEnable = true;
	// 0 response 1 request 2 all
	private XssFilterStrategy xssFilterStrategy;

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

	private void validateParam(Object domain) {
		if(validateParamEnable&&null!=domain) {
			paramValidator.validate(domain);
		}
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
					if (checkRequestXssStrategy(paramTypeClass)) {
						bodyString = xssSecurity.xssClean(bodyString);
					}
					param = ClassUtils.convertPrimitive(paramTypeClass, bodyString);
				}
			} else {
				if (body instanceof String) {
					param = serialization.deserialize((String) body, paramTypeClass);
					validateParam(param);
				} else {
					if (body instanceof MultiValueMap) {
						MultiValueMap<String, String> multiValueMap = (MultiValueMap<String, String>) body;
						param = serialization.convertValue(multiValueMap.toSingleValueMap(), paramTypeClass);
						multiValueMap.clear();
						validateParam(param);
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
						validateParam(param);
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

	private boolean checkRequestXssStrategy(Class<?> paramTypeClass) {
		return paramTypeClass == String.class && xssFilterEnable && xssFilterStrategy == XssFilterStrategy.REQUEST;
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
					if (checkRequestXssStrategy(paramTypeClass)) {
						paramValue = xssSecurity.xssClean(paramValue);
					}
					param = ClassUtils.convertPrimitive(paramTypeClass, paramValue);
				}
			} else {
				if (paramInfo.getParamFormat() == ParamFormat.MAP) {
					param = serialization.convertValue(mapPathParams, paramTypeClass);
					validateParam(param);
				} else {
					paramValue = mapPathParams.get(paramInfo.getParamName());
					param = serialization.deserialize(paramValue, paramTypeClass);
					validateParam(param);
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

	@Override
	public void afterPropertiesSet() throws Exception {
		xssFilterEnable = dubboReferenceConfigProperties.getSecurityConfig().isXssFilterEnable();
		xssFilterStrategy = XssFilterStrategy.values()[dubboReferenceConfigProperties.getSecurityConfig()
				.getXssFilterStrategy()];
		validateParamEnable=dubboReferenceConfigProperties.getSecurityConfig().isValidateParamEnable();

	}
}
