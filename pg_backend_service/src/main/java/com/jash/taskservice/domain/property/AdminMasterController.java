package com.jash.taskservice.domain.property;

import com.jash.taskservice.domain.user.UserMaster;
import com.jash.taskservice.domain.user.UserMasterRepository;
import com.jash.taskservice.core.exception.BusinessRuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AdminMasterController {

    private final UserMasterRepository userRepository;

    public AdminMasterController(UserMasterRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String plainPassword = credentials.get("password");

        UserMaster user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessRuleException("Access Denied: Bad credentials provided."));

        // 🛡️ Failsafe Guard: Persistent Lockout Check
        if (!user.isAccountNonLocked()) {
            throw new BusinessRuleException(
                "Whoa there, data cowboy. 🤠 We love your enthusiasm, but guessing passwords " +
                "like lottery numbers isn't working out. Your account is now safely locked down " +
                "for its own protection. Time to step away from the keyboard, contact Support, " +
                "and let a human help you out."
            );
        }

        // Check password match
        if (user.getPassword().equals(plainPassword)) {
            // Success Path: Clear failure counters
            if (user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
            return ResponseEntity.ok(Map.of(
                "status", "AUTHENTICATED",
                "message", "Welcome back, " + user.getFullName() + "!"
            ));
        } else {
            // Failure Path: Increment and evaluate
            int currentAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(currentAttempts);

            if (currentAttempts >= 5) {
                user.setAccountNonLocked(false); // Drop the lock gate
                userRepository.save(user);
                throw new BusinessRuleException(
                    "Whoa there, data cowboy. 🤠 We love your enthusiasm, but guessing passwords " +
                    "like lottery numbers isn't working out. Your account is now safely locked down " +
                    "for its own protection. Time to step away from the keyboard, contact Support, " +
                    "and let a human help you out."
                );
            }

            userRepository.save(user);
            int remaining = 5 - currentAttempts;
            
            // Sarcastic warning message for attempts 1 through 4
            throw new BusinessRuleException(
                "Access Denied. Your password guess was highly creative, but incorrect. " +
                "You have " + remaining + " attempts left before the system assumes you're an intruder " +
                "and handles you accordingly."
            );
        }
    }
}