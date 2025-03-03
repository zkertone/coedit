package com.coedit.service.intf;

import com.coedit.model.entity.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService  {
    public User reisterUser(User user);

    public User findByUsername(String username);

    public Optional<User> findById(String id);

    public Boolean existsByUsername(String username);

    public Boolean existsByEmail(String email);

    public String loginUser(String username, String password) throws UsernameNotFoundException, BadCredentialsException;;
}
