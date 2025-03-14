package com.example.MIMs.service;

import com.example.MIMs.entity.User;
import com.example.MIMs.repository.UserRepository;
import com.example.MIMs.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(User user) {
        try {
            logger.info("Registering user: " + user.getUsername());
            
            //Validate user input
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty.");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty.");
            }

            // Check if the username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
            }


            //Hash the password before saving
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            logger.info("Hashed Password: {}", hashedPassword);  // ✅ Log hashed password
    
            user.setPassword(hashedPassword);
            userRepository.save(user);
            
            logger.info("User registered successfully: {}", user.getUsername());  // ✅ Log successful registration
            return "User registered successfully!";
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {

        
            logger.error("Error during registration: {}" + e.getMessage(), e);  // ✅ Log error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
        }
    
    

    public String authenticate(String username, String password) {
        logger.info("Attempting login for user: {}", username);

        // Validate input
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password cannot empty");
        }
    
        Optional<User> user = userRepository.findByUsername(username);
    
        if (user.isPresent()) {
            logger.info("User found: {}" , user.get().getUsername());
            String storedHashedPassword = user.get().getPassword();
    
            logger.debug("Stored Hashed Password: {}", storedHashedPassword);
            logger.debug("Raw Input Password (before hashing): [PROTECTED]");
    
            // compare the plain password with the hashed password
            if (passwordEncoder.matches(password, storedHashedPassword)) {
                String token = jwtUtil.generateToken(username);
                logger.info("Generated JWT Token:for user: {}", username);  // ✅ Log token creation
                return token;
            } else {
                logger.warn("Invalid password for user: {}", username);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }
        }
    
        logger.warn("User not found: ", username);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
    }
}
