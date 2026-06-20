package com.jash.taskservice.domain.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PgPropertyRepository extends JpaRepository<PgProperty, Long> {
    Optional<PgProperty> findByPropertyCode(String propertyCode);
}