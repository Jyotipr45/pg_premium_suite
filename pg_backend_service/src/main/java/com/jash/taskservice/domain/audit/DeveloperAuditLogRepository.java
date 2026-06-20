package com.jash.taskservice.domain.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperAuditLogRepository extends JpaRepository<DeveloperAuditLog, Long> {
}