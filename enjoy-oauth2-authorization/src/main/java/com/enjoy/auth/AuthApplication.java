package com.enjoy.auth;

import java.security.Principal;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AuthApplication.class).web(true).run(args);
	}

	//
	@GetMapping("/hello")
	// @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String webHello() {
		return "Hello , Authorization";
	}

	@Configuration
	@EnableResourceServer
	public static class AuthResourceServer extends ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/me").authorizeRequests().anyRequest().authenticated();
		}

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.resourceId("resourceId");
		}
	}

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public Principal me(Principal principal) {
		return principal;
	}

}
