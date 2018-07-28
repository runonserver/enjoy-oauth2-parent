package com.enjoy.resource.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		return "Hello , I am Resource Server";
	}

}
