package com.atommiddleware.cloud.sample.provider;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@DubboComponentScan(basePackages = "com.atommiddleware.cloud.sample.provider")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
