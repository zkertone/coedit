package com.coedit.utils;

import com.coedit.service.intf.RedisTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtRedisAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtRedisAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                        UserDetailsService userDetailsService,
                                        RedisTokenService redisTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.redisTokenService = redisTokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtTokenProvider.validateToken(token)) {
                handleAuthenticationError(response, "Token无效或已过期");
                return;
            }

            if (!redisTokenService.isTokenValid(token)) {
                handleAuthenticationError(response, "Token已被注销或在其他设备登录");
                return;
            }

            String username = jwtTokenProvider.getUsernameFromToken(token);
            if (username == null || username.isEmpty()) {
                handleAuthenticationError(response, "Token中不包含有效的用户名");
                return;
            }
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                handleAuthenticationError(response, "无法加载用户详情");
                return;
            }
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            redisTokenService.tokenExpiration(token, 3600);
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleAuthenticationError(response, "认证处理过程中发生错误: " + e.getMessage());
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", message);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
