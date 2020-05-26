package com.bhaiti.uk.global.repo;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("global-repository")
public interface GRClient extends SSLConfig{
	//@GetMapping("/globalrepository/allforasite")
		//ResponseEntity<GlobalRepository> getAllRepsitoryForASite(@RequestParam(value="siteName",required = false) String siteName);
		
		@GetMapping("/globalrepository")
		 //String getGreeting(URI baseUrl);

		String getGreeting(URI determinedBasePathUri);
}
