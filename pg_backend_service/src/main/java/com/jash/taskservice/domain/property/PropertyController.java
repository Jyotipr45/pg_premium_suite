package com.jash.taskservice.domain.property;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PgProperty> createProperty(@RequestBody PgProperty property) {
        PgProperty savedProperty = propertyService.saveProperty(property);
        return new ResponseEntity<>(savedProperty, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<PgProperty>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'TENANT')")
    public ResponseEntity<PgProperty> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/{id}/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'TENANT')")
    public ResponseEntity<List<RoomMaster>> getRoomsByProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getRoomsByPropertyId(id));
    }
}