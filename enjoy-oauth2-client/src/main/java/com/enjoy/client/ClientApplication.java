package com.enjoy.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@EnableOAuth2Sso
@SpringBootApplication
public class ClientApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ClientApplication.class).web(true).run(args);
	}

	// 会加载
	@Autowired
	ResourceServerProperties resource;

	@Autowired
	AuthorizationCodeResourceDetails client;

	// oauth2resttemplate会通过加载到的yml配置信息，自动中继token
	@Bean
	// @LoadBalanced
	public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
		return new OAuth2RestTemplate(resource, context);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 目前禁用csrf
		http.csrf().disable().requestCache().requestCache(httpSessionRequestCache()).and()
				.addFilterAfter(oauth2ClientAuthenticationProcessingFilter(),
						AbstractPreAuthenticatedProcessingFilter.class)
				.authorizeRequests().antMatchers("/rest/session/expired**").permitAll().and().authorizeRequests()
				.antMatchers("/**").access("isAuthenticated()");
	}

	@Autowired
	private OAuth2RestTemplate oauth2RestTemplate;

	@Bean
	HttpSessionRequestCache httpSessionRequestCache() {
		HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
		/**
		 * 如果使用Java Config默认配置 ， 默认配置的是注释的几个请求匹配器
		 */
		httpSessionRequestCache
				.setRequestMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/**/favicon.ico")));
		return httpSessionRequestCache;
	}

	@Bean
	OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthenticationProcessingFilter() {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
				"/sso/login"); // 客户端拦截的sso登录拦截，类似spring security监听的/login请求
		filter.setRestTemplate(oauth2RestTemplate);
		// 获取授权服务端用户信息
		UserInfoTokenServices userInfoTokenServices = new UserInfoTokenServices(resource.getUserInfoUri(),
				client.getClientId());
		userInfoTokenServices.setRestTemplate(oauth2RestTemplate);
		filter.setTokenServices(userInfoTokenServices);

		return filter;
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

}
