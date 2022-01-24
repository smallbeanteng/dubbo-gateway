package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DubboReferenceConfigProperties.class)
public class DubboGatewayBootstrapConfiguration {

}
