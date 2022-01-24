package com.atommiddleware.cloud.autoconfigure;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;

public class DubboGateWayApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	
	private static AtomicBoolean atomicb=new AtomicBoolean(false);
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		if (atomicb.compareAndSet(false, true)) {
			AnnotationConfigApplicationContext annotationConfigApplicationContext = createContext(applicationContext);
			DubboReferenceConfigProperties dubboReferenceConfigProperties = annotationConfigApplicationContext
					.getBean(DubboReferenceConfigProperties.class);
			applicationContext.getBeanFactory().registerSingleton(
					DubboReferenceConfigProperties.class.getName(), dubboReferenceConfigProperties);
		}
	}

	protected AnnotationConfigApplicationContext createContext(
			ConfigurableApplicationContext parentApplicationContext) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.setEnvironment(parentApplicationContext.getEnvironment());
		context.setClassLoader(parentApplicationContext.getClassLoader());
		context.register(DubboGatewayBootstrapConfiguration.class);
		context.refresh();
		return context;
	}
}
