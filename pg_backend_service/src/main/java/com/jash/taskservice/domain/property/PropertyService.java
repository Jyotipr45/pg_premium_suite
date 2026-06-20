package com.jash.taskservice.domain.property;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PropertyService {

    private final PgPropertyRepository propertyRepository;
    private final RoomMasterRepository roomRepository;

    public PropertyService(PgPropertyRepository propertyRepository, RoomMasterRepository roomRepository) {
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
    }

    public PgProperty saveProperty(PgProperty property) {
        return propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public List<PgProperty> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PgProperty getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Property not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<RoomMaster> getRoomsByPropertyId(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new NoSuchElementException("Property not found with ID: " + propertyId);
        }
        return roomRepository.findByPropertyId(propertyId);
    }
}