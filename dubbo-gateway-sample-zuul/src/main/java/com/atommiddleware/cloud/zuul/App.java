package com.atommiddleware.cloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import com.atommiddleware.cloud.core.annotation.DubboGatewayScanner;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@DubboGatewayScanner(basePackages = "com.atommiddleware.cloud.sample.api")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
