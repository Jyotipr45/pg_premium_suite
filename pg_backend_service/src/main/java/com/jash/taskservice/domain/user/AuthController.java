package com.jash.taskservice.domain.user;

import com.jash.taskservice.core.config.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMasterRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserMasterRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            String username = credentials.get("username");
            
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, credentials.get("password"))
            );

            // Generate both tokens
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            // Save the refresh token state to the database row
            UserMaster user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User context lost during authentication mapping"));
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
            userRepository.save(user);

            // Create the secure HttpOnly cookie wrapper
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // ⚠️ Set to true in Production over HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 Days in seconds
            response.addCookie(cookie);

            // Return access token in the raw JSON body response
            Map<String, String> body = new HashMap<>();
            body.put("accessToken", accessToken);
            return ResponseEntity.ok(body);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Credentials Provided");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String tokenFromCookie = null;

        // Extract the token from cookies list
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    tokenFromCookie = cookie.getValue();
                    break;
                }
            }
        }

        if (tokenFromCookie == null || jwtUtil.isTokenExpired(tokenFromCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token missing or expired");
        }

        String username = jwtUtil.extractUsername(tokenFromCookie);
        UserMaster user = userRepository.findByUsername(username).orElse(null);

        // Verify token matches the database signature row explicitly to prevent reuse
        if (user == null || user.getRefreshToken() == null || !user.getRefreshToken().equals(tokenFromCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token signature reference");
        }

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validity context has expired");
        }

        // Issue a clean new short-lived access token
        String newAccessToken = jwtUtil.generateAccessToken(username);
        Map<String, String> body = new HashMap<>();
        body.put("accessToken", newAccessToken);
        return ResponseEntity.ok(body);
    }
}