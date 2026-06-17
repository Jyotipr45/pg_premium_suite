package com.jash.taskservice.repository;

import com.jash.taskservice.domain.model.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {
    Optional<UserMaster> findByUsername(String username);
    List<String> findByUserRole(String userRole);
    List<UserMaster> findByAssociatedPropertyId(Long propertyId);
}