package com.jash.taskservice.domain.user;

import com.jash.taskservice.core.config.JwtUtil;
import com.jash.taskservice.core.exception.BusinessRuleException;
import com.jash.taskservice.domain.mail.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMasterRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // 🕒 Thread-safe in-memory rate limiter cache to track velocity (1 request per minute)
    private final Map<String, LocalDateTime> rateLimitTracker = new ConcurrentHashMap<>();

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, 
                          UserMasterRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        UserMaster user = userRepository.findByUsername(username).orElse(null);

        if (user != null && !user.isAccountNonLocked()) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(
                "Whoa there, data cowboy. 🤠 We love your enthusiasm, but guessing passwords " +
                "like lottery numbers isn't working out. Your account is now safely locked down."
            );
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            if (user != null) {
                user.setFailedLoginAttempts(0);
                String accessToken = jwtUtil.generateAccessToken(username);
                String refreshToken = jwtUtil.generateRefreshToken(username);

                user.setRefreshToken(refreshToken);
                user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
                userRepository.save(user);

                Cookie cookie = new Cookie("refresh_token", refreshToken);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(cookie);

                Map<String, String> body = new HashMap<>();
                body.put("accessToken", accessToken);
                return ResponseEntity.ok(body);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User profile context error.");

        } catch (AuthenticationException e) {
            if (user != null) {
                int currentAttempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(currentAttempts);

                if (currentAttempts >= 5) {
                    user.setAccountNonLocked(false);
                    userRepository.save(user);
                    return ResponseEntity.status(HttpStatus.LOCKED).body(
                        "Whoa there, data cowboy. 🤠 Account locked down. Contact support."
                    );
                }

                userRepository.save(user);
                int remaining = 5 - currentAttempts;
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access Denied. Highly creative guess, but incorrect. " + remaining + " attempts left."
                );
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Credentials Provided");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessRuleException("Email target parameter missing.");
        }

        // 🛑 1. VELOCITY RATE LIMIT WINDOW CHECK (1 Request per 60 Seconds)
        LocalDateTime now = LocalDateTime.now();
        if (rateLimitTracker.containsKey(email)) {
            LocalDateTime lastRequestTime = rateLimitTracker.get(email);
            if (lastRequestTime.plusMinutes(1).isAfter(now)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Too Many Requests",
                    "message", "Cool down, engineer! 🛡️ System security policies allow 1 recovery request per minute. Check your inbox."
                ));
            }
        }
        rateLimitTracker.put(email, now);

        // Secure User Identifier Verification
        UserMaster user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleException("If that user identifier exists on our cloud ledger, an authorization link has been emitted."));

        // 🔐 2. CRYPTOGRAPHIC TOKEN GENERATION & HASHING
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashTokenSHA256(rawToken);

        // Persist ONLY the one-way hashed token safely inside PostgreSQL
        user.setResetToken(hashedToken);
        user.setResetTokenExpiry(now.plusMinutes(15));
        userRepository.save(user);

        // 🚀 3. ASYNC BACKGROUND THEAD WORKER DELIVERY PIPELINE
        emailService.sendRecoveryEmail(user.getEmail(), user.getFullName(), rawToken);

        return ResponseEntity.ok(Map.of("message", "A secure authorization token has been delivered to your destination inbox."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String rawToken = request.get("token");
        String newPassword = request.get("newPassword");
        String reEnterPassword = request.get("reEnterPassword");

        if (newPassword == null || !newPassword.equals(reEnterPassword)) {
            throw new BusinessRuleException("Password validation mismatch. Ensure both entry vectors match perfectly.");
        }

        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new BusinessRuleException("The verification authentication voucher signature is missing.");
        }

        // 🔐 4. HASH USER INPUT BEFORE VERIFYING AGAINST POSTGRESQL MATCHES
        String hashedInputToken = hashTokenSHA256(rawToken);

        UserMaster user = userRepository.findByResetToken(hashedInputToken)
                .orElseThrow(() -> new BusinessRuleException("The authentication recovery voucher signature is invalid or malicious."));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("The access token context validity timeline has expired. Please emit a fresh recovery sign.");
        }

        // Apply new credentials hashed via BCrypt safely
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); 
        user.setResetTokenExpiry(null);
        user.setAccountNonLocked(true); 
        user.setFailedLoginAttempts(0); 
        userRepository.save(user);

        // Instantly invalidate rate limiter map data window on successful reset completion
        rateLimitTracker.remove(user.getEmail());

        return ResponseEntity.ok(Map.of("message", "Access credentials updated successfully. You may log in normally."));
    }

    // SHA-256 One-way helper checksum algorithm block
    private String hashTokenSHA256(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Severe Runtime Security Failure: SHA-256 cryptographic algorithm engine unavailable.", e);
        }
    }
}