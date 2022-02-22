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
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties.RedisHttpSessionConfig.CookieConfig;

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
			CookieConfig cookieConfig=dubboReferenceConfigProperties.getSession().getCookie();
			if (!StringUtils.isEmpty(cookieConfig.getName())) {
				defaultCookieSerializer
						.setCookieName(cookieConfig.getName());
			}
			if (!StringUtils.isEmpty(cookieConfig.getDomain())) {
				defaultCookieSerializer
						.setDomainName(cookieConfig.getDomain());
			}
			if (!StringUtils.isEmpty(cookieConfig.getPath())) {
				defaultCookieSerializer
						.setCookiePath(cookieConfig.getPath());
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
			CookieConfig cookieConfig=dubboReferenceConfigProperties.getSession().getCookie();
			resolver.addCookieInitializer(o -> {
				if (!StringUtils.isEmpty(cookieConfig.getDomain())) {
					o.domain(cookieConfig.getDomain());
				}
				if (!StringUtils.isEmpty(cookieConfig.getPath())) {
					o.path(cookieConfig.getPath());
				}
			});
			if (!StringUtils.isEmpty(cookieConfig.getName())) {
				resolver.setCookieName(cookieConfig.getName());
			}
			return resolver;
		}
	}

}
