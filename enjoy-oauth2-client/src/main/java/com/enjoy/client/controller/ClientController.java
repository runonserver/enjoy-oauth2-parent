package com.enjoy.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@RequestMapping(value = "/call", method = RequestMethod.GET)
	public String callToResourceServer() {
		String result = "";
		try {
			result = restTemplate.getForObject("http://127.0.0.1:8092/resource/hello", String.class);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
}
