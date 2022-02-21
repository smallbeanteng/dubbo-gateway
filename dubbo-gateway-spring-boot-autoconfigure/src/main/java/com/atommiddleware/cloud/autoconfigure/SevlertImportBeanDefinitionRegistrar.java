package com.atommiddleware.cloud.autoconfigure;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.atommiddleware.cloud.core.controller.ForwardingServiceController;

public class SevlertImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(ForwardingServiceController.class.getSimpleName(),
				BeanDefinitionBuilder.genericBeanDefinition(ForwardingServiceController.class).getBeanDefinition());
	}
}
