package com.bhaiti.uk.global.repo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.Client;

@Configuration
public class CustomFeignConfiguration {

	@Bean
	public Client getfeignClient()
	{
	    Client trustSSLSockets = new Client.Default(
	            SSLUtil.getClientSSLSocketFactory(),
	            null);

	    System.out.println("feignClient called");
	    return trustSSLSockets;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
	        SpringClientFactory clientFactory) {
	    return new LoadBalancerFeignClient(new Client.Default(null, null),
	            cachingFactory, clientFactory);
	}
}
