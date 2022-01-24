package com.atommiddleware.cloud.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ParamAttribute(type = 3)
public @interface FromHeader {

	@AliasFor(annotation = ParamAttribute.class)
	String value() default "";

	@AliasFor(annotation = ParamAttribute.class)
	String name() default "";
	
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
}
