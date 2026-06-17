package com.jash.taskservice.controller;

import com.jash.taskservice.domain.exception.BusinessRuleException;
import com.jash.taskservice.domain.model.PropertyAsset;
import com.jash.taskservice.domain.model.PropertyExpense;
import com.jash.taskservice.repository.PgPropertyRepository;
import com.jash.taskservice.repository.PropertyAssetRepository;
import com.jash.taskservice.repository.PropertyExpenseRepository;
import com.jash.taskservice.repository.RoomMasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/owner/financials")
public class OwnerFinancialController {

    private final PropertyExpenseRepository expenseRepository;
    private final PropertyAssetRepository assetRepository;
    private final PgPropertyRepository propertyRepository;
    private final RoomMasterRepository roomRepository;

    public OwnerFinancialController(
            PropertyExpenseRepository expenseRepository,
            PropertyAssetRepository assetRepository,
            PgPropertyRepository propertyRepository,
            RoomMasterRepository roomRepository) {
        this.expenseRepository = expenseRepository;
        this.assetRepository = assetRepository;
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
    }

    // 1. Log a Recurring or One-off Operational Expense (OpEx)
    @PostMapping("/expenses")
    public ResponseEntity<PropertyExpense> logExpense(@RequestBody PropertyExpense expense) {
        if (expense.getPropertyId() == null || !propertyRepository.existsById(expense.getPropertyId())) {
            throw new BusinessRuleException("Cannot log expense: Target PG Property ID does not exist.");
        }
        PropertyExpense saved = expenseRepository.save(expense);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // 2. Onboard a physical Long-Term Capital Asset (CapEx)
    @PostMapping("/assets")
    public ResponseEntity<PropertyAsset> onboardAsset(@RequestBody PropertyAsset asset) {
        if (asset.getPropertyId() == null || !propertyRepository.existsById(asset.getPropertyId())) {
            throw new BusinessRuleException("Cannot onboard asset: Target PG Property ID does not exist.");
        }
        if (asset.getRoomId() != null && !roomRepository.existsById(asset.getRoomId())) {
            throw new BusinessRuleException("Cannot onboard asset: Specified Room ID does not exist.");
        }
        PropertyAsset saved = assetRepository.save(asset);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // 3. Aggregate Property Financial Summary for the Mobile Dashboard UI
    @GetMapping("/summary/{propertyId}")
    public ResponseEntity<Map<String, Object>> getPropertyFinancialSummary(@PathVariable Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new BusinessRuleException("Property not found with ID: " + propertyId);
        }

        List<PropertyExpense> expenses = expenseRepository.findByPropertyId(propertyId);
        List<PropertyAsset> assets = assetRepository.findByPropertyId(propertyId);

        double totalOpEx = expenses.stream().mapToDouble(PropertyExpense::getAmount).sum();
        double totalCapEx = assets.stream().mapToDouble(PropertyAsset::getPurchaseCost).sum();

        Map<String, Object> summaryReport = new HashMap<>();
        summaryReport.put("propertyId", propertyId);
        summaryReport.put("totalOperationalExpenses", totalOpEx);
        summaryReport.put("totalCapitalInvestment", totalCapEx);
        summaryReport.put("expenseCount", expenses.size());
        summaryReport.put("assetCount", assets.size());

        return ResponseEntity.ok(summaryReport);
    }
}