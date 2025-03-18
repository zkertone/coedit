package com.coedit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import com.coedit.utils.JwtTokenProvider;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    //注册STOMP端点
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")//客户端连接端点
                .setAllowedOrigins("*")//允许跨域
                .withSockJS();//支持SockJS回退（如浏览器不支持WebSocket）
    }

    //配置消息代理
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");//主题，队列；客户端订阅前缀
        config.setApplicationDestinationPrefixes("/app");//服务端接收消息前缀
        config.setUserDestinationPrefix("/user");//用户专属通道前缀
    }

    //在WebSocket握手前验证Token
    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (!jwtTokenProvider.validateToken(token)) {
                        throw new AuthenticationCredentialsNotFoundException("Invalid token");
                    }
                }
                return message;
            }
        });
    }
}
