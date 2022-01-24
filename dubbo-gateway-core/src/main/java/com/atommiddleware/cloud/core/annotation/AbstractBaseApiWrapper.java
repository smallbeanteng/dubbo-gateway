package com.atommiddleware.cloud.core.annotation;

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

	protected void convertBodyToParam(ParamInfo paramInfo, String body, Object[] params) {
		if (paramInfo.isRequired() && StringUtils.isEmpty(body)) {
			throw new IllegalArgumentException("body Parameter verification exception");
		}
		final Map<String, Class<?>> mapClasses = DubboApiContext.MAP_CLASSES;
		Object param = null;
		if (!StringUtils.isEmpty(body)) {
			Class<?> paramTypeClass = mapClasses.get(paramInfo.getParamType());
			if (paramTypeClass.isPrimitive()) {
				param = ClassUtils.convertPrimitive(paramTypeClass, body);
			} else {
				if (paramTypeClass == String.class) {
					param = body;
				} else {
					param = serialization.deserialize(body, paramTypeClass);
				}
			}
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
