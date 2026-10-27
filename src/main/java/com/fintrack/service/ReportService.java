package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.dao.TransactionDAO;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Calculates report aggregates from persisted transactions. */
public class ReportService {
    private final TransactionDAO transactionDAO;
    private final CategoryDAO categoryDAO;

    public ReportService(TransactionDAO transactionDAO, CategoryDAO categoryDAO) {
        this.transactionDAO = Objects.requireNonNull(transactionDAO, "transactionDAO");
        this.categoryDAO = Objects.requireNonNull(categoryDAO, "categoryDAO");
    }

    public Summary summary(LocalDate start, LocalDate end) {
        validateRange(start, end);
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        long transactionCount = 0;

        for (FinanceTransaction transaction : transactionDAO.findAll()) {
            if (transaction.getDate().isBefore(start) || transaction.getDate().isAfter(end)) continue;
            transactionCount++;
            if (transaction.getType() == TransactionType.INCOME) income = income.add(transaction.getAmount());
            else if (transaction.getType() == TransactionType.EXPENSE) expenses = expenses.add(transaction.getAmount());
        }

        BigDecimal net = income.subtract(expenses);
        BigDecimal savingsRate = income.signum() == 0
                ? null
                : net.multiply(BigDecimal.valueOf(100)).divide(income, 2, java.math.RoundingMode.HALF_UP);
        return new Summary(income, expenses, net, savingsRate, transactionCount);
    }

    public Map<String, BigDecimal> spendingByCategory(YearMonth month) {
        Objects.requireNonNull(month, "month");
        return spendingByCategory(month.atDay(1), month.atEndOfMonth());
    }

    /** Expense totals for an inclusive date range, ordered from largest to smallest. */
    public Map<String, BigDecimal> spendingByCategory(LocalDate start, LocalDate end) {
        validateRange(start, end);
        Map<Long, String> names = new java.util.HashMap<>();
        for (Category category : categoryDAO.findAll()) names.put(category.getId(), category.getName());

        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (FinanceTransaction transaction : transactionDAO.findAll()) {
            if (transaction.getType() != TransactionType.EXPENSE
                    || transaction.getDate().isBefore(start)
                    || transaction.getDate().isAfter(end)) continue;
            String name = transaction.getCategoryId() == null
                    ? "Uncategorised"
                    : names.getOrDefault(transaction.getCategoryId(), "Unknown category");
            totals.merge(name, transaction.getAmount(), BigDecimal::add);
        }

        List<Map.Entry<String, BigDecimal>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)));
        Map<String, BigDecimal> ordered = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : sorted) ordered.put(entry.getKey(), entry.getValue());
        return ordered;
    }

    private static void validateRange(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (start.isAfter(end)) throw new IllegalArgumentException("Report start date must be on or before the end date.");
    }

    public record Summary(BigDecimal income, BigDecimal expenses, BigDecimal netCashFlow,
                          BigDecimal savingsRatePercent, long transactionCount) {
        public boolean hasIncome() { return income.signum() > 0; }
    }
}
