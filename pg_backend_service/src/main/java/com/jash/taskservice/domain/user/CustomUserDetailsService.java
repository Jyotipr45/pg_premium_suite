package com.jash.taskservice.domain.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMasterRepository userRepository;

    public CustomUserDetailsService(UserMasterRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserMaster user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 🎯 FIX: Pass the real database password string dynamically instead of a hardcoded mock hash!
        return new User(
                user.getUsername(),
                user.getPassword(), 
                new ArrayList<>()
        );
    }
}