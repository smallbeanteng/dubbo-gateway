package com.atommiddleware.cloud.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.StringUtils;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(CookieSerializer.class)
@ConditionalOnProperty(prefix = "com.atommiddleware.cloud.config.session.cookie", name = "enable", havingValue = "true")
@AutoConfigureAfter(DubboGatewayCommonAutoConfiguration.class)
public class RedisHttpSessionAutoConfiguration {

	@Configuration
	@EnableRedisHttpSession
	@ConditionalOnWebApplication(type = Type.SERVLET)
	class SevlertRedisHttpSessionConfiguration {
		@Bean
		@ConditionalOnMissingBean
		public CookieSerializer cookieSerializer(DubboReferenceConfigProperties dubboReferenceConfigProperties) {
			DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
			if (!StringUtils.isEmpty(dubboReferenceConfigProperties.getSession().getCookie().getName())) {
				defaultCookieSerializer
						.setCookieName(dubboReferenceConfigProperties.getSession().getCookie().getName());
			}
			if (!StringUtils.isEmpty(dubboReferenceConfigProperties.getSession().getCookie().getDomain())) {
				defaultCookieSerializer
						.setDomainName(dubboReferenceConfigProperties.getSession().getCookie().getDomain());
			}
			if (!StringUtils.isEmpty(dubboReferenceConfigProperties.getSession().getCookie().getPath())) {
				defaultCookieSerializer
						.setCookiePath(dubboReferenceConfigProperties.getSession().getCookie().getPath());
			}
			return defaultCookieSerializer;
		}
	}

	@Configuration
	@EnableRedisWebSession
	@ConditionalOnWebApplication(type = Type.REACTIVE)
	class ReactiveRedisHttpSessionConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public WebSessionIdResolver webSessionIdResolver(
				DubboReferenceConfigProperties dubboReferenceConfigProperties) {
			CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
			resolver.addCookieInitializer(o -> {
				if (!StringUtils.isEmpty(dubboReferenceConfigProperties.getSession().getCookie().getDomain())) {
					o.domain(dubboReferenceConfigProperties.getSession().getCookie().getDomain());
				}
				if (!StringUtils.isEmpty(dubboReferenceConfigProperties.getSession().getCookie().getPath())) {
					o.path(dubboReferenceConfigProperties.getSession().getCookie().getPath());
				}
			});
			if (!StringUtils.isEmpty(dubboReferenceConfigProperties.getSession().getCookie().getName())) {
				resolver.setCookieName(dubboReferenceConfigProperties.getSession().getCookie().getName());
			}
			return resolver;
		}
	}

}
