package com.jash.taskservice.repository;

import com.jash.taskservice.domain.model.DeveloperAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperAuditLogRepository extends JpaRepository<DeveloperAuditLog, Long> {
}