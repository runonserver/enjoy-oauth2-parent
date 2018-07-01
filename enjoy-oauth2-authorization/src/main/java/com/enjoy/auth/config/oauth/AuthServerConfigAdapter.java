package com.enjoy.auth.config.oauth;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfigAdapter extends AuthorizationServerConfigurerAdapter {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private AuthenticationManager authenticationManager;

	// 声明 ClientDetails实现 - 基于内存
	@Bean
	public ClientDetailsService clientDetails() {
		return new InMemoryClientDetailsService();
	}

	// access_token存取方式 - 基于内存
	@Bean
	public TokenStore tokenStore() {
		return new RedisTokenStore(redisTemplate.getConnectionFactory());
	}

	// APP授权结果存储-基于内存
	@Bean
	public ApprovalStore approvalStore() {
		return new InMemoryApprovalStore();
	}

	// 授权码存储方式，基于内存
	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new InMemoryAuthorizationCodeServices();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager);
		endpoints.tokenStore(tokenStore());
		endpoints.setClientDetailsService(clientDetails());

		// 配置TokenServices参数
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(endpoints.getTokenStore());
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setReuseRefreshToken(false);
		tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
		tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
		// token 12个小时过期
		tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(12));

		endpoints.authorizationCodeServices(authorizationCodeServices());
		endpoints.approvalStore(approvalStore());
		endpoints.tokenServices(tokenServices);
	}

	/**
	 * 
	 * 
	 * 如果配置支持allowFormAuthenticationForClients的，且url中有client_id和client_secret的会走ClientCredentialsTokenEndpointFilter来保护
	 * 
	 * 如果没有支持allowFormAuthenticationForClients或者有支持但是url中没有client_id和client_secret的，走basic认证保护
	 * 
	 * 
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients();
	}

	/**
	 * 初始化clientApp到内存 类似于在第三方网站注册一个APP，客户端通过该APP获取第三方信息
	 * authorizedGrantTypes此处使用授权码模式，其他授权模式可咨询查阅资料
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("clientId").resourceIds("resourceId")
				.authorizedGrantTypes("authorization_code", "refresh_token").scopes("query").authorities("client")
				.secret("client123456").autoApprove(true);
	}

}
