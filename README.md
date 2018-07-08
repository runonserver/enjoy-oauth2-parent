# enjoy-oauth2-parent
Spring cloud security oauth sso

主要实现spring cloud security oauth2配置

access_token存放用户授权信息，通过redis关联，以达到授权服务与资源服务共享，防止token无效

enjoy-oauth2-authorization:授权服务 ， 继承spring security验证用户合法性，暴露OAuth2.0接口

enjoy-oauth2-client: OAuth2.0 服务使用方(客户端) ,使用授权方提供的用户信息，访问OAuth2.0保护的资源服务端

enjoy-oauth2-resource: 资源服务(业务资源接口) OAuth2保护的接口信息，需要access_token才能访问<br\>
