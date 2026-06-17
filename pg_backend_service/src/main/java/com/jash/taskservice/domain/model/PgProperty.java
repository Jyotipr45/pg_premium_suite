package com.jash.taskservice.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pg_properties")
public class PgProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String propertyCode; // e.g., "PG-PUNE-01", "PG-BLR-02"

    @Column(nullable = false)
    private String name;         // e.g., "Premium Suites"

    private Long locationId;     // Relational reference mapping back to LocationMaster.id
    private String description;
    private LocalDateTime createdAt;

    public PgProperty() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPropertyCode() { return propertyCode; }
    public void setPropertyCode(String propertyCode) { this.propertyCode = propertyCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}