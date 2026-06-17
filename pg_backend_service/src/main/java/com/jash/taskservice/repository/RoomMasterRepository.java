package com.jash.taskservice.repository;

import com.jash.taskservice.domain.model.RoomMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomMasterRepository extends JpaRepository<RoomMaster, Long> {
    List<RoomMaster> findByPropertyId(Long propertyId);
    List<RoomMaster> findByPropertyIdAndOccupiedFalse(Long propertyId);
}