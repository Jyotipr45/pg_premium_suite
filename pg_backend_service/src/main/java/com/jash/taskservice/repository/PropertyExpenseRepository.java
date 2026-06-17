package com.jash.taskservice.repository;

import com.jash.taskservice.domain.model.PropertyExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyExpenseRepository extends JpaRepository<PropertyExpense, Long> {
    List<PropertyExpense> findByPropertyId(Long propertyId);
}