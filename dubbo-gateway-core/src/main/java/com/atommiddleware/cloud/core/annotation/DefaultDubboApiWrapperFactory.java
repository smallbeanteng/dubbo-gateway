package com.atommiddleware.cloud.core.annotation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.dubbo.common.bytecode.CustomizedLoaderClassPath;
import org.apache.dubbo.common.utils.ClassUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.WebApplicationType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.atommiddleware.cloud.api.annotation.ParamAttribute;
import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFromType;
import com.atommiddleware.cloud.api.annotation.PathMapping;
import com.atommiddleware.cloud.core.config.DubboReferenceConfig;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import com.atommiddleware.cloud.core.context.DubboApiContext;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;

public class DefaultDubboApiWrapperFactory extends AbstractDubboApiWrapperFactory {

	private static AtomicLong WRAPPER_CLASS_COUNTER = new AtomicLong(0);
	  private static final Map<ClassLoader, ClassPool> POOL_MAP = new ConcurrentHashMap<ClassLoader, ClassPool>(); //ClassLoader - ClassPool
    public static ClassPool getClassPool(ClassLoader loader) {
        if (loader == null) {
            return ClassPool.getDefault();
        }

        ClassPool pool = POOL_MAP.get(loader);
        if (pool == null) {
            pool = new ClassPool(true);
            pool.appendClassPath(new CustomizedLoaderClassPath(loader));
            POOL_MAP.put(loader, pool);
        }
        return pool;
    }

	@Override
	public Class<?> make(String id, Class<?> interfaceClass,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, WebApplicationType webApplicationType)
			throws CannotCompileException, NotFoundException, IllegalArgumentException, IllegalAccessException,
			IOException {
		final Map<String, Class<?>> mapClasses = DubboApiContext.MAP_CLASSES;
		final Map<String, Map<ParamFromType, List<ParamInfo>>> mapParamInfo = DubboApiContext.MAP_PARAM_INFO;
		List<PathMappingMethodInfo> listPathMappingMethodInfo = new ArrayList<PathMappingMethodInfo>();
		Arrays.stream(interfaceClass.getMethods()).forEach(o -> {
			PathMapping pathMapping = AnnotationUtils.findAnnotation(o, PathMapping.class);
			if (null != pathMapping) {
				if (DubboApiContext.PATTERNS_REQUESTMETHOD.containsKey(pathMapping.path())) {
					throw new IllegalArgumentException("pathPatterns:[" + pathMapping.path() + "] repeat");
				}
				DubboApiContext.PATTERNS_REQUESTMETHOD.put(pathMapping.path(), pathMapping.requestMethod());
				PathMappingMethodInfo pathMappingMethodInfo = new PathMappingMethodInfo(o, pathMapping);
				if (o.getParameters().length > 0) {
					String[] strParamNames = getMethodParamName(o);
					Parameter[] parameters = o.getParameters();
					Class<?>[] typeParameters = o.getParameterTypes();
					int i = 0;
					boolean isSimpleType = true;
					boolean isAllChildSimpleType = true;
					Class<?> currentType = null;
					for (Parameter p : parameters) {
						if (!mapClasses.containsKey(typeParameters[i].getName())) {
							mapClasses.put(typeParameters[i].getName(), typeParameters[i]);
						}
						ParamAttribute an = AnnotatedElementUtils.getMergedAnnotation(p, ParamAttribute.class);
						if (null != an) {
							if (null == strParamNames) {
								if (StringUtils.isEmpty(an.name())
										&& an.paramFromType() != ParamFromType.FROM_BODY) {
									throw new IllegalArgumentException("ParamAttribute verification exception");
								}
							}
							currentType = typeParameters[i];
							isSimpleType = true;
							isAllChildSimpleType = true;
							if (ClassUtils.isSimpleType(currentType)) {
								isSimpleType = true;
								isAllChildSimpleType = true;
							} else {
								isSimpleType = false;
								// 遍历所有参数
								boolean flag = false;
								for (Field f : currentType.getDeclaredFields()) {
									flag = f.isAccessible();
									f.setAccessible(true);
									if (!ClassUtils.isSimpleType(f.getType())) {
										isAllChildSimpleType = false;
										f.setAccessible(flag);
										break;
									}
								}
								if (isAllChildSimpleType) {
									// 获取父类属性
									for (Field f : currentType.getFields()) {
										flag = f.isAccessible();
										f.setAccessible(true);
										if (!ClassUtils.isSimpleType(f.getType())) {
											isAllChildSimpleType = false;
											f.setAccessible(flag);
											break;
										}
									}
								}
							}
							// p.get
							pathMappingMethodInfo
									.getListParamMeta().add(
											new ParamMeta(
													an.paramFromType() == ParamFromType.FROM_BODY ? ""
															: StringUtils.isEmpty(an.name()) ? strParamNames[i]
																	: an.name(),
													typeParameters[i].getName(), an, isSimpleType,
													isAllChildSimpleType));

						} else {
							throw new IllegalArgumentException("ParamAttribute verification exception");
						}
						i++;
					}
				}
				listPathMappingMethodInfo.add(pathMappingMethodInfo);
			}
		});
		if (!CollectionUtils.isEmpty(listPathMappingMethodInfo)) {
			ClassPool pool =getClassPool(Thread.currentThread().getContextClassLoader());
			// id
			long idx = WRAPPER_CLASS_COUNTER.getAndIncrement();
			// class
			CtClass stuClass = pool.makeClass(interfaceClass.getName() + "$atommiddleware" + idx);
			if (webApplicationType == WebApplicationType.REACTIVE) {
				stuClass.setSuperclass(
						pool.getCtClass("com.atommiddleware.cloud.core.annotation.AbstractDubboApiWrapper"));
			} else {
				stuClass.setSuperclass(
						pool.getCtClass("com.atommiddleware.cloud.core.annotation.AbstractDubboApiServletWrapper"));
			}
			CtConstructor cons = new CtConstructor(new CtClass[] {}, stuClass);
			CtField h = new CtField(pool.getCtClass(interfaceClass.getName()), "h", stuClass);
			h.setModifiers(Modifier.PRIVATE);
			stuClass.addField(h);
			DubboReferenceConfig dubboReferenceConfig = dubboReferenceConfigProperties.getDubboRefer().get(id);
			ClassFile classFile = stuClass.getClassFile();
			ConstPool constPool = classFile.getConstPool();
			Annotation dubboReferenceAnnotation = new Annotation(DubboReference.class.getCanonicalName(), constPool);
			AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool,
					AnnotationsAttribute.visibleTag);
			dubboReferenceAnnotation.addMemberValue("async", new BooleanMemberValue(true, constPool));
			if (null != dubboReferenceConfig) {
				handlerAnnotaionParams(dubboReferenceConfig, dubboReferenceAnnotation, constPool);
			}
			annotationsAttribute.addAnnotation(dubboReferenceAnnotation);
			h.getFieldInfo().addAttribute(annotationsAttribute);
			String handleTypeClass = webApplicationType == WebApplicationType.REACTIVE
					? "org.springframework.web.server.ServerWebExchange"
					: "javax.servlet.http.HttpServletRequest";
			CtMethod ctMethod = new CtMethod(pool.getCtClass("java.util.concurrent.CompletableFuture"), "handler",
					new CtClass[] { pool.getCtClass("java.lang.String"), pool.getCtClass(handleTypeClass),
							pool.getCtClass("java.lang.Object") },
					stuClass);
			ctMethod.setModifiers(Modifier.PUBLIC);
			StringBuilder strBody = new StringBuilder();
			StringBuilder strParamTemp = new StringBuilder();
			StringBuilder strParamTempConstructor = new StringBuilder();
			strParamTempConstructor.append("{");
			strBody.append("{");
			for (PathMappingMethodInfo pathMappingMethodInfo : listPathMappingMethodInfo) {
				strParamTemp.setLength(0);
				String path = pathMappingMethodInfo.getPathMapping().path();
				strParamTempConstructor.append(" patterns.add(\"" + path + "\"); ");
				strBody.append(" if($1.equals(\"" + path + "\")){");
				if (!CollectionUtils.isEmpty(pathMappingMethodInfo.getListParamMeta())) {
					List<ParamInfo> listParamInfo = new ArrayList<ParamInfo>();
					strBody.append(" java.lang.Object[] params=new java.lang.Object["
							+ pathMappingMethodInfo.getListParamMeta().size() + "]; ");
					int i = 0;
					ParamInfo paramInfo;
					for (ParamMeta paramMeta : pathMappingMethodInfo.getListParamMeta()) {
						paramInfo = new ParamInfo(i, paramMeta.getParamName(), paramMeta.getParamAttribute().paramFromType(),
								paramMeta.getParamType(),paramMeta.getParamAttribute().paramFormat(),paramMeta.isSimpleType(), paramMeta.isChildAllSimpleType(),
								paramMeta.getParamAttribute().required());
						listParamInfo.add(paramInfo);
						strParamTemp.append("(" + paramMeta.getParamType() + ")params[" + i + "],");
						i++;
					}
					mapParamInfo.put(path, listParamInfo.stream()
							.collect(Collectors.groupingBy(ParamInfo::getParamFromType, Collectors.toList())));
					strBody.append(" handlerConvertParams(\"" + path + "\",$2,params,$3); ");
					strBody.append(" h." + pathMappingMethodInfo.getMethod().getName() + "(");
					strBody.append(strParamTemp.deleteCharAt(strParamTemp.length() - 1).toString());
					strBody.append(");");
				} else {
					strBody.append(" h." + pathMappingMethodInfo.getMethod().getName() + "(");
					strBody.append(");");
				}
				strBody.append("}");
			}
			strParamTempConstructor.append("}");
			strBody.append(" return org.apache.dubbo.rpc.RpcContext.getContext().getCompletableFuture(); ");
			strBody.append(" } ");
			ctMethod.setBody(strBody.toString());
			stuClass.addMethod(ctMethod);
			cons.setBody(strParamTempConstructor.toString());
			stuClass.addConstructor(cons);
			return stuClass.toClass();
		}
		return null;
	}
}
