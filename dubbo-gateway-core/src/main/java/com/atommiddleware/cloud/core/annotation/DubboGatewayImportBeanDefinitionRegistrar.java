package com.atommiddleware.cloud.core.annotation;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.api.annotation.GateWayDubbo;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.context.DubboApiContext;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DubboGatewayImportBeanDefinitionRegistrar
		implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware, BeanFactoryAware, Ordered {

	private ResourceLoader resourceLoader;

	private Environment environment;

	private DubboReferenceConfigProperties dubboReferenceConfigProperties;

	private BeanFactory beanFactory;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		dubboReferenceConfigProperties = beanFactory.getBean(DubboReferenceConfigProperties.class);
		try {
			registerWrapper(importingClassMetadata, registry);
		} catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | CannotCompileException
				| NotFoundException | IOException e) {
			log.error("registerwrapper fail", e);
		}
		registerDubboGatewayPostProcessor(registry);
	}

	private void registerDubboGatewayPostProcessor(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(DubboGatewayPostProcessor.class.getName(),
				BeanDefinitionBuilder.genericBeanDefinition(DubboGatewayPostProcessor.class).getBeanDefinition());
	}
	public ClassLoader getClassLoader() {
		if (this.resourceLoader != null) {
			return this.resourceLoader.getClassLoader();
		}
		return ClassUtils.getDefaultClassLoader();
	}
	public void registerWrapper(AnnotationMetadata metadata, BeanDefinitionRegistry registry)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, CannotCompileException,
			NotFoundException, IOException {
		LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		scanner.setResourceLoader(this.resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(GateWayDubbo.class));
		Set<String> basePackages = getBasePackages(metadata);
		for (String basePackage : basePackages) {
			candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
		}
		Class<?> classWrapper = null;
		Map<String, Object> attributes;
		AnnotatedBeanDefinition beanDefinition;
		AnnotationMetadata annotationMetadata;
		for (BeanDefinition candidateComponent : candidateComponents) {
			if (candidateComponent instanceof AnnotatedBeanDefinition) {
				beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
				annotationMetadata = beanDefinition.getMetadata();
				attributes = annotationMetadata.getAnnotationAttributes(GateWayDubbo.class.getCanonicalName());
				Object objId = attributes.get("id");
				DubboApiWrapperFactory dubboApiWrapperFactory=SpringFactoriesLoader.loadFactories(DubboApiWrapperFactory.class, getClassLoader()).get(0);
				DubboApiContext.CHARSET=dubboReferenceConfigProperties.getCharset();
				classWrapper = dubboApiWrapperFactory.make(null != objId ? String.valueOf(objId) : null,
						Class.forName(candidateComponent.getBeanClassName()), dubboReferenceConfigProperties,DubboGateWayApplicationListener.WEBAPPLICATIONTYPE);
				if (null != classWrapper) {
					registry.registerBeanDefinition(classWrapper.getSimpleName(),
							BeanDefinitionBuilder.genericBeanDefinition(classWrapper).getBeanDefinition());
				} else {
					if (log.isWarnEnabled()) {
						log.warn("BeanClassName:[{}] has not method mapping", candidateComponent.getBeanClassName());
					}
				}
			}
		}
	}

	protected ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				boolean isCandidate = false;
				if (beanDefinition.getMetadata().isIndependent()) {
					if (!beanDefinition.getMetadata().isAnnotation()) {
						isCandidate = true;
					}
				}
				return isCandidate;
			}
		};
	}

	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> attributes = importingClassMetadata
				.getAnnotationAttributes(DubboGatewayScanner.class.getCanonicalName());

		Set<String> basePackages = new HashSet<>();
		for (String pkg : (String[]) attributes.get("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		return basePackages;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;

	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
