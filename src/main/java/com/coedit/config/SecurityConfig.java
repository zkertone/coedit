package com.coedit.config;

import com.coedit.service.intf.RedisTokenService;
import com.coedit.utils.JwtRedisAuthenticationFilter;
import com.coedit.utils.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
                          UserDetailsService userDetailsService,
                          RedisTokenService redisTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.redisTokenService = redisTokenService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() //允许公开访问的端点
                        .anyRequest().authenticated() //其他请求需要认证
                )
                .addFilterBefore(
                        new JwtRedisAuthenticationFilter(jwtTokenProvider,userDetailsService,redisTokenService),
                        UsernamePasswordAuthenticationFilter.class //将JWT过滤器添加到过滤器链中
                );

        return http.build();
    }


}
