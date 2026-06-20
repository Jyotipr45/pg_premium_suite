package com.jash.taskservice.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueMasterRepository extends JpaRepository<IssueMaster, Long> {
    List<IssueMaster> findByReportedByUserId(Long userId);
    List<IssueMaster> findByPropertyId(Long propertyId);
    List<IssueMaster> findByStatus(String status);
}