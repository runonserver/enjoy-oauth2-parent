package com.enjoy.resource;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ResourceApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ResourceApplication.class).web(true).run(args);
	}

}
