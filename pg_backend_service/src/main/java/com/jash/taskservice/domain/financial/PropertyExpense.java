package com.jash.taskservice.domain.financial;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_expenses")
public class PropertyExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long propertyId;          // Relational reference to PgProperty.id

    @Column(nullable = false)
    private String title;             // e.g., "Electricity Bill", "Caretaker Salary"

    private double amount;
    private String expenseCategory;   // e.g., "UTILITY", "SALARY", "MARKETING"
    private String paymentStatus;     // e.g., "PENDING", "PAID"
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;

    public PropertyExpense() {
        this.paymentStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getExpenseCategory() { return expenseCategory; }
    public void setExpenseCategory(String expenseCategory) { this.expenseCategory = expenseCategory; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}