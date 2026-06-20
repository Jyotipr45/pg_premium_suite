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
        String username = credentials.get("username");

        // Look up the user target profile
        UserMaster user = userRepository.findByUsername(username).orElse(null);

        // 🛡️ Failsafe Guard 1: Sarcastic Lockout Message Trigger
        if (user != null && !user.isAccountNonLocked()) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(
                "Whoa there, data cowboy. 🤠 We love your enthusiasm, but guessing passwords " +
                "like lottery numbers isn't working out. Your account is now safely locked down " +
                "for its own protection. Time to step away from the keyboard, contact Support, " +
                "and let a human help you out."
            );
        }

        try {
            // Attempt to authenticate through Spring Security manager
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, credentials.get("password"))
            );

            // Success Path: Reset tracking metrics
            if (user != null && user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }

            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            if (user != null) {
                user.setRefreshToken(refreshToken);
                user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
                userRepository.save(user);
            }

            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);

            Map<String, String> body = new HashMap<>();
            body.put("accessToken", accessToken);
            return ResponseEntity.ok(body);

        } catch (AuthenticationException e) {
            // Failure Path: Increment attempts if user exists
            if (user != null) {
                int currentAttempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(currentAttempts);

                if (currentAttempts >= 5) {
                    user.setAccountNonLocked(false); // Lock the account
                    userRepository.save(user);
                    return ResponseEntity.status(HttpStatus.LOCKED).body(
                        "Whoa there, data cowboy. 🤠 We love your enthusiasm, but guessing passwords " +
                        "like lottery numbers isn't working out. Your account is now safely locked down " +
                        "for its own protection. Time to step away from the keyboard, contact Support, " +
                        "and let a human help you out."
                    );
                }

                userRepository.save(user);
                int remaining = 5 - currentAttempts;
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access Denied. Your password guess was highly creative, but incorrect. " +
                    "You have " + remaining + " attempts left before the system assumes you're an intruder " +
                    "and handles you accordingly."
                );
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Credentials Provided");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String tokenFromCookie = null;
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

        if (user == null || user.getRefreshToken() == null || !user.getRefreshToken().equals(tokenFromCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token signature reference");
        }

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validity context has expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(username);
        Map<String, String> body = new HashMap<>();
        body.put("accessToken", newAccessToken);
        return ResponseEntity.ok(body);
    }
}