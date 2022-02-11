package com.atommiddleware.cloud.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFromType;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ParamAttribute(paramFromType = ParamFromType.FROM_BODY)
public @interface FromBody {
	
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
}
