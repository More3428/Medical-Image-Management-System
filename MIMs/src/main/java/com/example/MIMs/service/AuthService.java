package com.example.MIMs.service;

import com.example.MIMs.model.User;
import com.example.MIMs.repository.UserRepository;
import com.example.MIMs.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(User user) {
        System.out.println("Registering user: " + user.getUsername());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return "User registered successfully!";
    }

    public String authenticate(String username, String password) {
        System.out.println("Attempting login for user: " + username);
    
        Optional<User> user = userRepository.findByUsername(username);
    
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getUsername());
            String storedHashedPassword = user.get().getPassword();
    
            System.out.println("Stored Hashed Password: " + storedHashedPassword);
            System.out.println("Raw Input Password (before hashing): " + password);
    
            // ✅ FIX: Ensure we compare the plain password with the hashed password
            if (passwordEncoder.matches(password, storedHashedPassword)) {
                String token = jwtUtil.generateToken(username);
                System.out.println("Generated JWT Token: " + token);  // ✅ Log token creation
                return token;
            } else {
                System.out.println("Invalid password for user: " + username);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }
        }
    
        System.out.println("User not found: " + username);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
    }
}
