package com.coedit.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private Long expiresIn;
    private String username;
    private List<String> roles;
}
