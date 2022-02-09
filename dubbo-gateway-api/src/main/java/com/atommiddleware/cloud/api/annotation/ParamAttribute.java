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
	
	int type();
	
	int paramFormat() default 1;
	
	public enum ParamFormat {
		MAP, JSON
	}
}
