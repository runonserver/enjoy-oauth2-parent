package com.enjoy.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启@PreAuthorize注解功能
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				// 开启表单登录和登出操作
				.formLogin().and().logout().logoutUrl("/logout").permitAll()
				// 拦截所有请求
				.and().authorizeRequests().anyRequest().authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 初始化内存用户 , spring security 默认跟角色加 ROLE_ 前缀
		auth.inMemoryAuthentication()
				//
				.withUser("admin").password("qwe123").roles("ADMIN")
				//
				.and().withUser("user").password("asd123").roles("USER");
	}

}
