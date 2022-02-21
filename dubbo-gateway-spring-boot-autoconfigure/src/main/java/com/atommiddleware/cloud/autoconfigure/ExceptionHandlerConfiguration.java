package com.atommiddleware.cloud.autoconfigure;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import com.atommiddleware.cloud.core.exception.JsonExceptionHandler;
import com.atommiddleware.cloud.security.validation.ParamValidator;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config", name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(DubboGatewayAutoConfiguration.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class ExceptionHandlerConfiguration {

    private final ServerProperties serverProperties;

    private final ApplicationContext applicationContext;

    private final ResourceProperties resourceProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    private final ParamValidator paramValidator;
    public ExceptionHandlerConfiguration(ServerProperties serverProperties,
                                         ResourceProperties resourceProperties,
                                         ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                         ServerCodecConfigurer serverCodecConfigurer,
                                         ApplicationContext applicationContext,ParamValidator paramValidator) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
        this.paramValidator=paramValidator;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        JsonExceptionHandler exceptionHandler = new JsonExceptionHandler(
                errorAttributes,
                this.resourceProperties,
                this.serverProperties.getError(),
                this.applicationContext,this.paramValidator);
        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler; 
    }
}
