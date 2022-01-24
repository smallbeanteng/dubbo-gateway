package com.atommiddleware.cloud.core.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atommiddleware.cloud.api.annotation.PathMapping;
import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.core.annotation.DubboApiServletWrapper;
import com.atommiddleware.cloud.core.annotation.DubboApiWrapper;
import com.atommiddleware.cloud.core.annotation.ParamInfo;

public class DubboApiContext {

	public final static Map<String, DubboApiWrapper> MAP_DUBBO_API_WRAPPER = new HashMap<String, DubboApiWrapper>();
	public final static Map<String, DubboApiWrapper> MAP_DUBBO_API_PATH_PATTERN_WRAPPER = new HashMap<String, DubboApiWrapper>();
	public final static Map<String, RequestMethod> PATTERNS_REQUESTMETHOD = new HashMap<String, PathMapping.RequestMethod>();

	public static Map<String, Map<Integer, List<ParamInfo>>> MAP_PARAM_INFO = new HashMap<String, Map<Integer, List<ParamInfo>>>();
	public static Map<String, Class<?>> MAP_CLASSES = new HashMap<String, Class<?>>();

	public static String CHARSET = "UTF-8";

	public final static Map<String, DubboApiServletWrapper> MAP_DUBBO_API_SERVLET_WRAPPER = new HashMap<String, DubboApiServletWrapper>();
	public final static Map<String, DubboApiServletWrapper> MAP_DUBBO_API_PATH_PATTERN_SERVLET_WRAPPER = new HashMap<String, DubboApiServletWrapper>();
}
