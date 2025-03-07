package com.coedit.service.intf;

import com.coedit.model.entity.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户服务接口
 * 提供用户相关的业务操作，包括注册、登录、查询等功能
 */
@Service
public interface UserService  {
    /**
     * 注册新用户
     * @param user 待注册的用户信息
     * @return 注册成功的用户信息
     */
    public User reisterUser(User user);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 查找到的用户信息
     */
    public User findByUsername(String username);

    /**
     * 根据用户ID查找用户
     * @param id 用户ID
     * @return 可能包含用户信息的Optional对象
     */
    public Optional<User> findById(String id);

    /**
     * 检查用户名是否已存在
     * @param username 待检查的用户名
     * @return 如果用户名已存在返回true，否则返回false
     */
    public Boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已被注册
     * @param email 待检查的邮箱
     * @return 如果邮箱已被注册返回true，否则返回false
     */
    public Boolean existsByEmail(String email);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功后的JWT令牌
     * @throws UsernameNotFoundException 当用户名不存在时抛出
     * @throws BadCredentialsException 当密码错误时抛出
     */
    public String loginUser(String username, String password) throws UsernameNotFoundException, BadCredentialsException;
}
