package com.jash.taskservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.jash.taskservice.domain.exception.BusinessRuleException;
import com.jash.taskservice.domain.model.*;
import com.jash.taskservice.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/master")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMasterController {

    private final LocationMasterRepository locationRepository;
    private final PgPropertyRepository propertyRepository;
    private final RoomMasterRepository roomRepository;
    private final UserMasterRepository userRepository;
    private final UserDocumentRepository documentRepository;

    // Explicit constructor-based dependency injection
    public AdminMasterController(
            LocationMasterRepository locationRepository,
            PgPropertyRepository propertyRepository,
            RoomMasterRepository roomRepository,
            UserMasterRepository userRepository,
            UserDocumentRepository documentRepository) {
        this.locationRepository = locationRepository;
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    // --- 1. Location Lookup Master Mappings ---
    @PostMapping("/locations")
    public ResponseEntity<LocationMaster> createLocation(@RequestBody LocationMaster location) {
        LocationMaster saved = locationRepository.save(location);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LocationMaster>> getAllActiveLocations() {
        return ResponseEntity.ok(locationRepository.findByActiveTrue());
    }

    // --- 2. PG Property Master Mappings ---
    @PostMapping("/properties")
    public ResponseEntity<PgProperty> onboardProperty(@RequestBody PgProperty property) {
        if (property.getLocationId() == null || !locationRepository.existsById(property.getLocationId())) {
            throw new BusinessRuleException("Cannot onboard property: Provided Location ID does not exist.");
        }
        PgProperty saved = propertyRepository.save(property);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PgProperty>> getAllProperties() {
        return ResponseEntity.ok(propertyRepository.findAll());
    }

    // --- 3. Room Inventory Configuration Mappings ---
    @PostMapping("/rooms")
    public ResponseEntity<RoomMaster> configureNewRoom(@RequestBody RoomMaster room) {
        if (room.getPropertyId() == null || !propertyRepository.existsById(room.getPropertyId())) {
            throw new BusinessRuleException("Cannot add room: Associated PG Property ID does not exist.");
        }
        RoomMaster saved = roomRepository.save(room);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // --- 4. User Registry & Profile Provisioning Mappings ---
    @PostMapping("/users")
    public ResponseEntity<UserMaster> registerNewUserProfile(@RequestBody UserMaster user) {
        if (user.getAssociatedPropertyId() != null && !propertyRepository.existsById(user.getAssociatedPropertyId())) {
            throw new BusinessRuleException("Cannot register user: Associated Property ID does not exist.");
        }
        UserMaster saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // --- 5. KYC Document Verification Gate ---
    @PatchMapping("/documents/{id}/verify")
    public ResponseEntity<UserDocument> toggleKycVerificationStatus(
            @PathVariable Long id, 
            @RequestParam boolean verified) {
            
        UserDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Target document asset entry not found with ID: " + id));

        document.setVerified(verified);
        UserDocument updatedDoc = documentRepository.save(document);

        // Relational chain effect: If a user's core lease or ID document is verified, check overall user registry flag
        if (verified) {
            userRepository.findById(document.getUserId()).ifPresent(user -> {
                user.setKycVerified(true);
                userRepository.save(user);
            });
        }

        return ResponseEntity.ok(updatedDoc);
    }
}