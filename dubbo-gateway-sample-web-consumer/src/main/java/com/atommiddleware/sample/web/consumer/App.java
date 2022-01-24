package com.atommiddleware.sample.web.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.atommiddleware.cloud.core.annotation.DubboGatewayScanner;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@DubboGatewayScanner(basePackages = "com.atommiddleware.cloud.sample.api")
public class App 
{
    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }
}
