package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.dao.TransactionDAO;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private final TransactionDAO transactionDAO;
    private final CategoryDAO categoryDAO;
    public ReportService(TransactionDAO transactionDAO, CategoryDAO categoryDAO) { this.transactionDAO = transactionDAO; this.categoryDAO = categoryDAO; }
    public Map<String, BigDecimal> spendingByCategory(YearMonth month) {
        Map<Long, String> names = new java.util.HashMap<>();
        for (Category category : categoryDAO.findAll()) names.put(category.getId(), category.getName());
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (FinanceTransaction tx : transactionDAO.findAll()) {
            if (tx.getType() != TransactionType.EXPENSE || !YearMonth.from(tx.getDate()).equals(month)) continue;
            String name = tx.getCategoryId() == null ? "Uncategorised" : names.getOrDefault(tx.getCategoryId(), "Unknown category");
            result.merge(name, tx.getAmount(), BigDecimal::add);
        }
        return result;
    }
}
