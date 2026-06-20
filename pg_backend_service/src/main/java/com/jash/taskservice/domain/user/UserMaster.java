package com.jash.taskservice.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_masters")
public class UserMaster {
    @jakarta.persistence.Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @jakarta.persistence.Column(name = "refresh_token_expiry")
    private java.time.LocalDateTime refreshTokenExpiry;

    @jakarta.persistence.Column(name = "password")
    private String password;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;       // Maps back to our JWT token Principal identity

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String emergencyContact;
    private String userRole;       // e.g., "ADMIN", "CARETAKER", "TENANT", "COOK"
    
    private Long associatedPropertyId; // Links staff/tenants directly to a PgProperty.id
    private Long assignedRoomId;       // Nullable (Staff don't occupy rooms, residents do)
    
    private boolean kycVerified;
    private LocalDateTime createdAt;

    public UserMaster() {
        this.kycVerified = false;
        this.createdAt = LocalDateTime.now();
    }

    // Standard Explicit Getters and Setters (Lombok-Free Compliance)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRefreshToken() { return this.refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public java.time.LocalDateTime getRefreshTokenExpiry() { return this.refreshTokenExpiry; }
    public void setRefreshTokenExpiry(java.time.LocalDateTime refreshTokenExpiry) { this.refreshTokenExpiry = refreshTokenExpiry; }
}