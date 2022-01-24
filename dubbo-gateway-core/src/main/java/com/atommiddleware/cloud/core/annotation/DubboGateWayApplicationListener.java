package com.atommiddleware.cloud.core.annotation;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

public class DubboGateWayApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>,Ordered{

	public static volatile WebApplicationType WEBAPPLICATIONTYPE;
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		if(null==WEBAPPLICATIONTYPE) {
			WEBAPPLICATIONTYPE=event.getSpringApplication().getWebApplicationType();
		}
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

}
