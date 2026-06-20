package com.jash.taskservice.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_documents")
public class UserDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;           // Foreign relational key reference back to UserMaster.id

    @Column(nullable = false)
    private String documentType;   // e.g., "AADHAAR", "PAN", "LEASE_AGREEMENT"

    @Column(nullable = false)
    private String fileName;       // e.g., "aadhaar_back_9001.pdf"

    @Column(nullable = false)
    private String fileStoragePath;// Absolute local/cloud object destination URI folder pointer

    private String contentType;     // e.g., "application/pdf", "image/jpeg"
    private boolean verified;
    private LocalDateTime uploadedAt;

    public UserDocument() {
        this.verified = false;
        this.uploadedAt = LocalDateTime.now();
    }

    // Standard Explicit Getters and Setters (Lombok-Free Compliance)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileStoragePath() { return fileStoragePath; }
    public void setFileStoragePath(String fileStoragePath) { this.fileStoragePath = fileStoragePath; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}