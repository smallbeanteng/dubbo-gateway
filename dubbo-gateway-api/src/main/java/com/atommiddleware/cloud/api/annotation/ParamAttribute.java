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

	@AliasFor("name")
	String value() default "";

	@AliasFor("value")
	String name() default "";
	
	boolean required() default true;
	
	ParamFromType paramFromType();
	
	ParamFormat paramFormat() default ParamFormat.MAP;
	
	public enum ParamFormat {
		MAP, JSON
	}
	public enum ParamFromType {
		FROM_BODY, FROM_COOKIE, FROM_HEADER, FROM_PATH, FROM_ATTRIBUTE, FROM_QUERYPARAMS;
	}
}
