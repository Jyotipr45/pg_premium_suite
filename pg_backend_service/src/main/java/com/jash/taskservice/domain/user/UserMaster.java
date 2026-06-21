package com.jash.taskservice.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_masters")
public class UserMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    private String emergencyContact;
    private String userRole; // e.g., "ADMIN", "CARETAKER", "TENANT", "COOK"

    private Long associatedPropertyId;
    private Long assignedRoomId;

    private boolean kycVerified;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    private LocalDateTime createdAt;

    // 📧 Password recovery context tracking definitions
    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private java.time.LocalDateTime resetTokenExpiry;

    public UserMaster() {
        this.kycVerified = false;
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getRefreshTokenExpiry() { return refreshTokenExpiry; }
    public void setRefreshTokenExpiry(LocalDateTime refreshTokenExpiry) { this.refreshTokenExpiry = refreshTokenExpiry; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public Long getAssociatedPropertyId() { return associatedPropertyId; }
    public void setAssociatedPropertyId(Long associatedPropertyId) { this.associatedPropertyId = associatedPropertyId; }

    public Long getAssignedRoomId() { return assignedRoomId; }
    public void setAssignedRoomId(Long assignedRoomId) { this.assignedRoomId = assignedRoomId; }

    public boolean isKycVerified() { return kycVerified; }
    public void setKycVerified(boolean kycVerified) { this.kycVerified = kycVerified; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public java.time.LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(java.time.LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }
}