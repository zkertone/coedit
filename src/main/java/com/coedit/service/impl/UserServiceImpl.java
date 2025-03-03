package com.coedit.service.impl;

import com.coedit.model.entity.User;
import com.coedit.repository.UserRepository;
import com.coedit.service.intf.UserService;
import com.coedit.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  JwtTokenProvider jwtTokenProvider;



//Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.查询用户
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        //2.构建Spring Security的USerDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0]))
                .build();

    }
    //业务方法
    @Override
    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("用户未找到"));
    }
    @Override
    public User reisterUser(User user) {
        //1.检查用户名是否存在
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("用户名已被注册");
        }
        //2.检查邮箱是否已存在
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("邮箱已被注册");
        }
        //3.密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //4.设置默认角色
        user.setRoles(Collections.singletonList("ROLE_USER"));
        //5.保存用户
        return userRepository.save(user);
    }

    @Override
    public String loginUser(String username, String password) {
        //1.查询用户
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        //2.验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        //3.生成JWT令牌
        return jwtTokenProvider.generateToken(user.getUsername());
    }

    @Override
    public Optional<User> findById(String id){
        return userRepository.findById(id);
    }

    @Override
    public Boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
