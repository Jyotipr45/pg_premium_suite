package com.jash.taskservice.repository;

import com.jash.taskservice.domain.model.LocationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationMasterRepository extends JpaRepository<LocationMaster, Long> {
    List<LocationMaster> findByActiveTrue();
}