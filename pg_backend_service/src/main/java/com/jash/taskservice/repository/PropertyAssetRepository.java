package com.jash.taskservice.repository;

import com.jash.taskservice.domain.model.PropertyAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyAssetRepository extends JpaRepository<PropertyAsset, Long> {
    List<PropertyAsset> findByPropertyId(Long propertyId);
    List<PropertyAsset> findByRoomId(Long roomId);
}