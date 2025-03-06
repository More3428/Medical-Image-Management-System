package com.example.MIMs.controller;

import com.example.MIMs.model.User;
import com.example.MIMs.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        String message = authService.registerUser(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        
        return ResponseEntity.ok(response);  // ✅ Returns JSON response
    }


    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        
        String token = authService.authenticate(username, password);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        
        return ResponseEntity.ok(response);  // ✅ Returns JSON response
    }
}
