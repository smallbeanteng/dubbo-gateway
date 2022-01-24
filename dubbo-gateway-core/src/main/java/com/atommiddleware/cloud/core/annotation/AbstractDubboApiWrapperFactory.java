package com.atommiddleware.cloud.core.annotation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.WebApplicationType;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import com.atommiddleware.cloud.core.config.DubboReferenceConfig;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.config.ReferenceMethodConfig;
import com.atommiddleware.cloud.core.config.ReferenceMethodConfig.ReferenceArgument;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public abstract class AbstractDubboApiWrapperFactory implements DubboApiWrapperFactory{

	@Override
	public Class<?> make(String id, Class<?> interfaceClass,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, WebApplicationType webApplicationType)
			throws CannotCompileException, NotFoundException, IllegalArgumentException, IllegalAccessException,
			IOException {
		throw new UnsupportedOperationException();
	}
	
	protected String[] getMethodParamName(Method method) {
		method.setAccessible(true);
		DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
		return discoverer.getParameterNames(method);
	}
	
	protected void handlerAnnotaionParams(DubboReferenceConfig dubboReferenceConfig,
			Annotation dubboReferenceAnnotation, ConstPool constPool)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = dubboReferenceConfig.getClass().getDeclaredFields();
		boolean accessFlag = false;
		Object fieldValue = null;
		for (Field field : fields) {
			accessFlag = field.isAccessible();
			if (!field.isAccessible())
				field.setAccessible(true);
			fieldValue = field.get(dubboReferenceConfig);
			if (null != fieldValue) {
				handlerCommon(field.getName(), fieldValue, dubboReferenceAnnotation, constPool);
				if (fieldValue instanceof ReferenceMethodConfig[]) {
					ReferenceMethodConfig[] arrayReferenceMethodConfig = (ReferenceMethodConfig[]) fieldValue;
					if (arrayReferenceMethodConfig.length > 0) {
						List<Annotation> annotations = new ArrayList<Annotation>();
						for (ReferenceMethodConfig referenceMethodConfig : arrayReferenceMethodConfig) {
							annotations.add(createDubboMethodAnnotation(referenceMethodConfig, constPool));
						}
						AnnotationMemberValue[] annotationMemberValues = new AnnotationMemberValue[annotations.size()];
						int i = 0;
						for (Annotation annotationTemp : annotations) {
							annotationMemberValues[i] = new AnnotationMemberValue(annotationTemp, constPool);
							i++;
						}
						ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
						arrayMemberValue.setValue(annotationMemberValues);
						dubboReferenceAnnotation.addMemberValue("methods", arrayMemberValue);
					}
				}
			}

			field.setAccessible(accessFlag);
		}
	}

	protected void handlerCommon(String fieldName, Object fieldValue, Annotation annotation, ConstPool constPool) {
		if (fieldValue instanceof String) {
			String fieldValueStr = String.valueOf(fieldValue);
			if (!StringUtils.isEmpty(fieldValueStr)) {
				annotation.addMemberValue(fieldName, new StringMemberValue(fieldValueStr, constPool));
			}
		}
		if (fieldValue instanceof Integer) {
			int fieldValueInteger = ((Integer) fieldValue).intValue();
			if (fieldValueInteger > 0) {
				annotation.addMemberValue(fieldName, new IntegerMemberValue(constPool, (Integer) fieldValueInteger));
			}
		}
		if (fieldValue instanceof Boolean) {
			boolean filedValueBoolean = (Boolean) fieldValue;
			annotation.addMemberValue(fieldName, new BooleanMemberValue(filedValueBoolean, constPool));
		}
		if (fieldValue instanceof String[]) {
			String[] filedValueArray = (String[]) fieldValue;
			if (filedValueArray.length > 0) {
				StringMemberValue[] stringMemberValues = new StringMemberValue[filedValueArray.length];
				int i = 0;
				for (String fieldValueStr : filedValueArray) {
					stringMemberValues[i] = new StringMemberValue(fieldValueStr, constPool);
					i++;
				}
				ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
				arrayMemberValue.setValue(stringMemberValues);
				annotation.addMemberValue(fieldName, arrayMemberValue);
			}
		}
	}

	protected Annotation createDubboMethodAnnotation(ReferenceMethodConfig referenceMethodConfig,
			ConstPool constPool) throws IllegalArgumentException, IllegalAccessException {
		Annotation annotation = new Annotation(org.apache.dubbo.config.annotation.Method.class.getCanonicalName(),
				constPool);
		Field[] fields = referenceMethodConfig.getClass().getDeclaredFields();
		boolean accessFlag = false;
		Object fieldValue = null;
		for (Field field : fields) {
			accessFlag = field.isAccessible();
			if (!field.isAccessible())
				field.setAccessible(true);
			fieldValue = field.get(referenceMethodConfig);
			if (null != fieldValue) {
				handlerCommon(field.getName(), fieldValue, annotation, constPool);
				if (fieldValue instanceof ReferenceArgument[]) {
					ReferenceArgument[] arrayReferenceArgument = (ReferenceArgument[]) fieldValue;
					if (arrayReferenceArgument.length > 0) {
						List<Annotation> annotations = new ArrayList<Annotation>();
						for (ReferenceArgument referenceArgument : arrayReferenceArgument) {
							annotations.add(createDubboArgumentAnnotation(referenceArgument, constPool));
						}
						AnnotationMemberValue[] annotationMemberValues = new AnnotationMemberValue[annotations.size()];
						int i = 0;
						for (Annotation annotationTemp : annotations) {
							annotationMemberValues[i] = new AnnotationMemberValue(annotationTemp, constPool);
							i++;
						}
						ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
						arrayMemberValue.setValue(annotationMemberValues);
						annotation.addMemberValue("arguments", arrayMemberValue);
					}
				}
			}

			field.setAccessible(accessFlag);
		}
		return annotation;
	}

	protected Annotation createDubboArgumentAnnotation(ReferenceArgument referenceArgument, ConstPool constPool)
			throws IllegalArgumentException, IllegalAccessException {
		Annotation annotation = new Annotation(org.apache.dubbo.config.annotation.Argument.class.getCanonicalName(),
				constPool);
		Field[] fields = referenceArgument.getClass().getDeclaredFields();
		boolean accessFlag = false;
		Object fieldValue = null;
		for (Field field : fields) {
			accessFlag = field.isAccessible();
			if (!field.isAccessible())
				field.setAccessible(true);
			fieldValue = field.get(referenceArgument);
			if (null != fieldValue) {
				handlerCommon(field.getName(), fieldValue, annotation, constPool);
			}
			field.setAccessible(accessFlag);
		}
		return annotation;
	}


}
