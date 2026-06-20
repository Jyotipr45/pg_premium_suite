package com.jash.taskservice.domain;

import com.jash.taskservice.domain.user.UserMaster;
import com.jash.taskservice.domain.user.UserMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMasterRepository userRepository;

    private String dynamicUsername;

    @BeforeEach
    public void setupTestData() {
        // Generate isolated unique variables to prevent collision with database entries
        dynamicUsername = "cowboy_" + UUID.randomUUID().toString().substring(0, 8);
        String dynamicPhone = "9" + String.format("%09d", (long) (Math.random() * 1000000000L));

        // Clean up any user matching our specific target to protect test integrity
        userRepository.findByUsername(dynamicUsername).ifPresent(userRepository::delete);
        
        UserMaster testUser = new UserMaster();
        testUser.setUsername(dynamicUsername);
        testUser.setPassword("secure_pass_123");
        testUser.setFullName("Isolated Test Cowboy");
        testUser.setPhoneNumber(dynamicPhone);
        testUser.setUserRole("ROLE_TENANT");
        testUser.setAccountNonLocked(true);
        testUser.setFailedLoginAttempts(0);
        
        userRepository.saveAndFlush(testUser);
    }

    @Test
    public void testAccountLockoutFlowAfterFiveFailedAttempts() throws Exception {
        // Construct payload referencing our dynamic run profile
        String badPayload = String.format("{\"username\":\"%s\", \"password\":\"completely_wrong_pass\"}", dynamicUsername);

        // 🎯 Attempts 1 to 4: Should return 401 Unauthorized with the remaining countdown warning
        for (int i = 1; i <= 4; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badPayload))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(containsString("attempts left")));
        }

        // 🎯 Attempt 5: The limit is crossed! Account drops the gate and throws our custom sarcastic lockout response
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badPayload))
                .andExpect(status().isLocked())
                .andExpect(content().string(containsString("data cowboy. 🤠")));
    }
}