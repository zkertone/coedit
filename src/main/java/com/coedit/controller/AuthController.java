package com.coedit.controller;

import com.coedit.model.entity.User;
import com.coedit.service.intf.RedisTokenService;
import com.coedit.service.intf.UserService;
import com.coedit.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RedisTokenService redisTokenService;

    public AuthController(JwtTokenProvider jwtTokenProvider,
                          UserService userService,
                          RedisTokenService redisTokenService
            ){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.redisTokenService = redisTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
    try{
        // 1. 验证用户名密码（伪代码）
       userService.loginUser(user.getUsername(), user.getPassword());
        // 2. 生成Token
        String token = jwtTokenProvider.generateToken(user.getUsername());
        redisTokenService.storeToken(token,user.getId(),3600);
        //3.返回响应
        return ResponseEntity.ok(Map.of( "accessToken", token));
    }catch (UsernameNotFoundException | BadCredentialsException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用户名或密码错误"));

    }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register( @RequestBody User user) {
        // 1. 调用服务层注册用户
        User registeredUser = userService.reisterUser(user);

        // 2. 返回响应
        return ResponseEntity.created(URI.create("/users/" + registeredUser.getId()))
                .body(Map.of(
                        "id", registeredUser.getId(),
                        "username", registeredUser.getUsername(),
                        "email", registeredUser.getEmail()
                ));
    }
}
