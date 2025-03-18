package com.coedit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null) {
            response.put("authenticated", authentication.isAuthenticated());
            response.put("principal", authentication.getPrincipal());
            response.put("name", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
        } else {
            response.put("authenticated", false);
            response.put("message", "No authentication found");
        }
        
        return ResponseEntity.ok(response);
    }
}
