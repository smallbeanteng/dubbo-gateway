package com.atommiddleware.cloud.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamAttribute {

	/**
	 * 参数 名称
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 参数 名称
	 */
	@AliasFor("value")
	String name() default "";
	
	/**
	 * 是否检查参数
	 */
	boolean required() default true;
	
	int type();
}
