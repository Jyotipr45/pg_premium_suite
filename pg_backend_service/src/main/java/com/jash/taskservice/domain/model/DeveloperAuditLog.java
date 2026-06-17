package com.jash.taskservice.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dev_audit_logs")
public class DeveloperAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientIp;
    private String requestUrl;
    private String httpMethod;
    private int httpStatus;
    
    @Column(columnDefinition = "TEXT")
    private String exceptionMessage;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    private LocalDateTime timestamp;

    public DeveloperAuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    // Traditional explicit Getters and Setters (Lombok-Free)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public int getHttpStatus() { return httpStatus; }
    public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }

    public String getExceptionMessage() { return exceptionMessage; }
    public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }

    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}