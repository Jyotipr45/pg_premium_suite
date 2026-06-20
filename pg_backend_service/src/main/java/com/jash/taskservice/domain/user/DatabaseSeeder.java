package com.jash.taskservice.domain.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserMasterRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inject PasswordEncoder directly into the constructor constructor
    public DatabaseSeeder(UserMasterRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔄 Checking database layer for missing demo accounts...");
        
        if (!userRepository.existsByUsername("postgres")) {
            System.out.println("🌱 Initial context detected. Auto-seeding core demo user account with hashed credentials...");
            
            UserMaster demoUser = new UserMaster();
            demoUser.setFullName("Demo Administrator");
            demoUser.setUsername("postgres");
            
            // 🎯 Dynamically encode the password using the application's bean setup!
            demoUser.setPassword(passwordEncoder.encode("Godspeed")); 
            
            demoUser.setPhoneNumber("9999999999");
            demoUser.setUserRole("ADMIN");
            demoUser.setKycVerified(true);
            demoUser.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(demoUser);
            System.out.println("✅ Complete! Pristine demo profile successfully loaded into user_masters.");
        } else {
            System.out.println("ℹ️ User tables contain active datasets. Seeding skipped.");
        }
    }
}