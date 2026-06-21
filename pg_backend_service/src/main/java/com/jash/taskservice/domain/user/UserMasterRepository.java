package com.jash.taskservice.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {
    Optional<UserMaster> findByUsername(String username);
    List<String> findByUserRole(String userRole);
    List<UserMaster> findByAssociatedPropertyId(Long propertyId);

    boolean existsByUsername(String username);


    java.util.Optional<UserMaster> findByResetToken(String resetToken);

    java.util.Optional<UserMaster> findByEmail(String email);
}