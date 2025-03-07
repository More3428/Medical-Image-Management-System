package com.example.MIMs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtUtil {
    private final String SECRET_KEY_STRING;
    private final SecretKey SECRET_KEY;

    public JwtUtil() {
        SECRET_KEY_STRING = System.getenv("JWT_SECRET");

        if (SECRET_KEY_STRING == null || SECRET_KEY_STRING.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set. Please configure it before running the application.");
        }

        try {
            byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
            this.SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize JWT secret key: " + e.getMessage(), e);
        }
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour expiration
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}