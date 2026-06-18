package com.jash.taskservice.controller;

import com.jash.taskservice.domain.exception.BusinessRuleException;
import com.jash.taskservice.domain.model.*;
import com.jash.taskservice.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/dashboard")
public class MobileDashboardController {

    private final UserMasterRepository userRepository;
    private final PgPropertyRepository propertyRepository;
    private final RoomMasterRepository roomRepository;
    private final UserDocumentRepository documentRepository;

    public MobileDashboardController(
            UserMasterRepository userRepository,
            PgPropertyRepository propertyRepository,
            RoomMasterRepository roomRepository,
            UserDocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
        this.documentRepository = documentRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<MobileDashboardDto> getMobileDashboardData(Principal principal) {
        // Double-Layer Security check: Extract username context implicitly from safe Principal
        UserMaster user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new BusinessRuleException("Profile context missing for user identity: " + principal.getName()));

        MobileDashboardDto dto = new MobileDashboardDto();
        dto.setResidentName(user.getFullName());
        dto.setUserRole(user.getUserRole());
        dto.setKycVerified(user.isKycVerified());

        // Relational Extraction 1: Map property constraints safely if assigned
        if (user.getAssociatedPropertyId() != null) {
            propertyRepository.findById(user.getAssociatedPropertyId()).ifPresent(property -> {
                dto.setPropertyName(property.getName());
                dto.setPropertyCode(property.getPropertyCode());
            });
        }

        // Relational Extraction 2: Map room indices to identify the billing metrics
        // In a true multi-tenant environment, query by individual tenant assignment logs
        roomRepository.findAll().stream()
                .filter(room -> user.getAssociatedPropertyId() != null && room.getPropertyId().equals(user.getAssociatedPropertyId()))
                .findFirst()
                .ifPresent(room -> {
                    dto.setRoomNumber(room.getRoomNumber());
                    dto.setMonthlyRent(room.getMonthlyRent());
                });

        // Relational Extraction 3: Collect full file metadata traces for the KYC attachment panel
        List<UserDocument> documents = documentRepository.findByUserId(user.getId());
        dto.setUploadedDocuments(documents);

        return ResponseEntity.ok(dto);
    }
}