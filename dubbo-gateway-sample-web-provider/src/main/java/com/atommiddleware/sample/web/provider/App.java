package com.atommiddleware.sample.web.provider;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DubboComponentScan(basePackages = "com.atommiddleware.sample.web.provider")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
