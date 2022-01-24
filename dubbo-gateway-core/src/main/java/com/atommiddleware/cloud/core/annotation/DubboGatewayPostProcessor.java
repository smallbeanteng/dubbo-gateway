package com.atommiddleware.cloud.core.annotation;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.PathMatcher;

import com.atommiddleware.cloud.core.context.DubboApiContext;

public class DubboGatewayPostProcessor implements SmartInitializingSingleton, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Autowired
	private PathMatcher pathMatcher = null;

	@Override
	public void afterSingletonsInstantiated() {
		Map<String, DubboApiWrapper> mapDubboApiWrapper = applicationContext.getBeansOfType(DubboApiWrapper.class);
		for (Map.Entry<String, DubboApiWrapper> entry : mapDubboApiWrapper.entrySet()) {
			for (String pathPattern : entry.getValue().getPathPatterns()) {
				if (pathMatcher.isPattern(pathPattern)) {
					DubboApiContext.MAP_DUBBO_API_PATH_PATTERN_WRAPPER.put(pathPattern, entry.getValue());
				} else {
					DubboApiContext.MAP_DUBBO_API_WRAPPER.put(pathPattern, entry.getValue());
				}
			}
		}

		Map<String, DubboApiServletWrapper> mapDubboApiServletWrapper = applicationContext
				.getBeansOfType(DubboApiServletWrapper.class);
		for (Map.Entry<String, DubboApiServletWrapper> entry : mapDubboApiServletWrapper.entrySet()) {
			for (String pathPattern : entry.getValue().getPathPatterns()) {
				if (pathMatcher.isPattern(pathPattern)) {
					DubboApiContext.MAP_DUBBO_API_PATH_PATTERN_SERVLET_WRAPPER.put(pathPattern, entry.getValue());
				} else {
					DubboApiContext.MAP_DUBBO_API_SERVLET_WRAPPER.put(pathPattern, entry.getValue());
				}
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
