package com.bhaiti.uk.global.repo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Controller
public class GRController {

	@Autowired
	GRClient grClient;
	
	@Autowired
	private EurekaClient eurekaClient;
	 
	public String getBaseUrl() {
	    Application application =
	      eurekaClient.getApplication("global-repository");
	    InstanceInfo instanceInfo = application.getInstances().get(0);
	    //String hostname = instanceInfo.getHostName();
	    String url = instanceInfo.getHomePageUrl();
	    //int port = instanceInfo.getPort();
	    //System.out.println(instanceInfo.);
	    return url;//"https://" + hostname + ":" + port;
	}

	
	@GetMapping(path="/globalrepository", produces = "application/json")
	public ResponseEntity<String> getGRGreeting() {
		URI determinedBasePathUri = URI.create(getBaseUrl());
		String resp = grClient.getGreeting(determinedBasePathUri);
		return ResponseEntity.ok().body(resp);
	}
	
	@GetMapping(path="/gr", produces = "application/json")
	public ResponseEntity<String> getGrGreeting() {
		SSLClient sslClient = SSLClient.getSSLClient();	
		String strurl ="https://localhost:56000/globalrepository";
		URL url = null;
		try {
			url = new URL(strurl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String method = "GET";
		String message = "";// can be json or xml
		String msgtype = "text/xml";
		String response = sslClient.sendRequest(url, method, message, msgtype);
		String resp = "Welcome to GR Client";
		return ResponseEntity.ok().body(response);
	}
}
