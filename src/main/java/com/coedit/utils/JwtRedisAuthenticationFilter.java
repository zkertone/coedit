package com.coedit.utils;

import com.coedit.service.intf.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRedisAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;

    public JwtRedisAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                        UserDetailsService userDetailsService,
                                        RedisTokenService redisTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.redisTokenService = redisTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
try{
    //1.从请求头提取Token
    String token = extractToken(request);

    if (token != null && jwtTokenProvider.validateToken(token) && redisTokenService.isTokenValid(token)) {
        //2.验证Token并获取用户名
        String username = jwtTokenProvider.getUsernameFromToken(token);
        //3.加载用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //4.设置认证上下文，当前请求已经通过身份认证，且 userDetails 代表当前用户
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //延长token有效期
        redisTokenService.tokenExpiration(token,3600);
    }
}catch (Exception e){
    //Token校验失败，清理上下文
    SecurityContextHolder.clearContext();
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期");
    return;
}
filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        //从请求头 Authorization 中提取 Token，格式要求为 Bearer <token>
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
