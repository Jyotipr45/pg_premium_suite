package com.jash.taskservice.domain;

import com.jash.taskservice.domain.financial.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class FinancialServiceTest {

    @Autowired
    private FinancialService financialService;

    @Autowired
    private PropertyAssetRepository assetRepository;

    @Autowired
    private PropertyExpenseRepository expenseRepository;

    @Test
    public void testCalculateTotalCapitalOutlayAggregatesCorrectly() {
        Long testPropertyId = 9999L;

        // Clean slate for testing context stability
        assetRepository.deleteAll();
        expenseRepository.deleteAll();

        // Seed 1: Physical Capital Asset Investment (purchaseCost: 15,500.00)
        PropertyAsset asset = new PropertyAsset();
        asset.setPropertyId(testPropertyId);
        asset.setAssetName("LG Refrigerator");
        asset.setPurchaseCost(15500.0);
        assetRepository.save(asset);

        // Seed 2: Operational Expense Outflow (amount: 4,500.00)
        PropertyExpense expense = new PropertyExpense();
        expense.setPropertyId(testPropertyId);
        expense.setTitle("Electricity Bill");
        expense.setAmount(4500.0);
        expense.setExpenseCategory("UTILITY");
        expenseRepository.save(expense);

        // Execute aggregation method calculation target
        double totalOutlay = financialService.calculateTotalCapitalOutlay(testPropertyId);

        // Assert 15500.00 + 4500.00 results in precisely 20000.00
        assertEquals(20000.0, totalOutlay, "The financial service failed to combine capital inventory with bills correctly.");
    }
}