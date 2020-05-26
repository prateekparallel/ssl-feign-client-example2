package com.bhaiti.uk.global.repo;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sslClient", configuration = CustomFeignConfiguration.class)
public interface SSLConfig {
	
}
