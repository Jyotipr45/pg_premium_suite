package com.jash.taskservice.domain.financial;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FinancialService {

    private final PropertyAssetRepository assetRepository;
    private final PropertyExpenseRepository expenseRepository;

    public FinancialService(PropertyAssetRepository assetRepository, PropertyExpenseRepository expenseRepository) {
        this.assetRepository = assetRepository;
        this.expenseRepository = expenseRepository;
    }

    public PropertyAsset recordAssetEntry(PropertyAsset asset) {
        return assetRepository.save(asset);
    }

    public PropertyExpense recordExpenseEntry(PropertyExpense expense) {
        return expenseRepository.save(expense);
    }

    // 📊 Calculates total capital outlay (Physical assets + Operational costs)
    @Transactional(readOnly = true)
    public double calculateTotalCapitalOutlay(Long propertyId) {
        double totalAssetInvestment = assetRepository.findAll().stream()
                .filter(asset -> asset.getPropertyId() != null && asset.getPropertyId().equals(propertyId))
                .mapToDouble(PropertyAsset::getPurchaseCost) // 🎯 Using your correct purchaseCost getter
                .sum();

        double totalOperationalExpenses = expenseRepository.findAll().stream()
                .filter(exp -> exp.getPropertyId() != null && exp.getPropertyId().equals(propertyId))
                .mapToDouble(PropertyExpense::getAmount)     // 🎯 Using your correct amount getter
                .sum();

        return totalAssetInvestment + totalOperationalExpenses;
    }
}