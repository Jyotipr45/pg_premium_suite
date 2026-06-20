package com.jash.taskservice.domain.dashboard;

import java.util.List;

import com.jash.taskservice.domain.user.UserDocument;

public class MobileDashboardDto {

    private String residentName;
    private String userRole;
    private boolean kycVerified;
    private String propertyName;
    private String propertyCode;
    private String roomNumber;
    private double monthlyRent;
    private List<UserDocument> uploadedDocuments;

    public MobileDashboardDto() {}

    // Standard Explicit Getters and Setters (Lombok-Free Compliance)
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public boolean isKycVerified() { return kycVerified; }
    public void setKycVerified(boolean kycVerified) { this.kycVerified = kycVerified; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public String getPropertyCode() { return propertyCode; }
    public void setPropertyCode(String propertyCode) { this.propertyCode = propertyCode; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }

    public List<UserDocument> getUploadedDocuments() { return uploadedDocuments; }
    public void setUploadedDocuments(List<UserDocument> uploadedDocuments) { this.uploadedDocuments = uploadedDocuments; }
}