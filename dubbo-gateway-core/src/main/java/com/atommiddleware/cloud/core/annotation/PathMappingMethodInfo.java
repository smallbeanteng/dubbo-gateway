package com.atommiddleware.cloud.core.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.atommiddleware.cloud.api.annotation.PathMapping;

public class PathMappingMethodInfo {
	
	public PathMappingMethodInfo(Method method,PathMapping pathMapping) {
		this.method=method;
		this.pathMapping=pathMapping;
	}
	
	private Method method;
	
	private PathMapping pathMapping;
	
	private List<ParamMeta> listParamMeta=new ArrayList<ParamMeta>();
	
	public List<ParamMeta> getListParamMeta() {
		return listParamMeta;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public PathMapping getPathMapping() {
		return pathMapping;
	}

	public void setPathMapping(PathMapping pathMapping) {
		this.pathMapping = pathMapping;
	}
	
	
}
