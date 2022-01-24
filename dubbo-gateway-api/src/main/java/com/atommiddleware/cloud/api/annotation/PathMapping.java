package com.atommiddleware.cloud.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathMapping {

	@AliasFor("path")
	String value() default "";

	@AliasFor("value")
	String path() default "";
	
	RequestMethod requestMethod() default RequestMethod.POST;
	
	public enum RequestMethod {
		GET, POST

	}
}
