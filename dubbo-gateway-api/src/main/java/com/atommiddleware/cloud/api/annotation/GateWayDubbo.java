package com.atommiddleware.cloud.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GateWayDubbo {

	/**
	 * 服务id,可以和dubbo普通调用的配置属性关联.
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * 服务id,可以和dubbo普通调用的配置属性关联.
	 * 例如: 
			com.atommiddleware.cloud.config.dubboRefer.<userService>.version=1.1.0
			com.atommiddleware.cloud.config.dubboRefer.<userService>.group=userSystem
			以上相当于会调用版本号为1.1.0并且groupw为userSystem的dubbo服务,与@DubboReference的参数对齐，具体支持哪些参数详见配置类
	 */
	@AliasFor("value")
	String id() default "";
}
