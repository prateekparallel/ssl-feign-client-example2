package com.bhaiti.uk.global.repo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class GrClientApplication {

	public static void main(String[] args) {
		SSLUtil.setupSslContext();
		SpringApplication.run(GrClientApplication.class, args);
	}

}
