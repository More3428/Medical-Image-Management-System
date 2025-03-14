package com.example.MIMs.controller;

import com.example.MIMs.entity.User;
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
    try {
        System.out.println("Registering user: " + user.getUsername());

        String message = authService.registerUser(user);

        // Authenticate the user after registration to generate a token
        String token = authService.authenticate(user.getUsername(), user.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("token", token);

        System.out.println("Registration successful, sending response: " + response);
        return ResponseEntity.ok(response);  // ✅ Returns JSON response
    } catch (Exception e) {
        System.err.println("Error during registration: " + e.getMessage());
        e.printStackTrace();
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Registration failed: " + e.getMessage());
        return ResponseEntity.status(500).body(errorResponse);
    }
}
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        try{

        
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        
        String token = authService.authenticate(username, password);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        System.out.println("Login successful, sending token: " + response);
        return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }

    }
}
