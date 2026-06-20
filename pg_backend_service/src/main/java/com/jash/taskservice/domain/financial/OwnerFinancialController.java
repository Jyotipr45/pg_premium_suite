package com.jash.taskservice.domain.financial;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/financial")
public class OwnerFinancialController {

    private final FinancialService financialService;

    public OwnerFinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    @PostMapping("/assets")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<PropertyAsset> addAssetEntry(@RequestBody PropertyAsset asset) {
        return new ResponseEntity<>(financialService.recordAssetEntry(asset), HttpStatus.CREATED);
    }

    @PostMapping("/expenses")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<PropertyExpense> addExpenseEntry(@RequestBody PropertyExpense expense) {
        return new ResponseEntity<>(financialService.recordExpenseEntry(expense), HttpStatus.CREATED);
    }

    // 🎯 Fixed case sensitivity to standard Spring Boot @GetMapping annotation
    @GetMapping("/summary/{propertyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Double> getPropertySummary(@PathVariable Long propertyId) {
        double currentOutlay = financialService.calculateTotalCapitalOutlay(propertyId);
        return ResponseEntity.ok(currentOutlay);
    }
}