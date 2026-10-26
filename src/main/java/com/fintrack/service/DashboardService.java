package com.fintrack.service;

import com.fintrack.dao.TransactionDAO;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardService {
    private final TransactionDAO transactionDAO;
    public DashboardService(TransactionDAO transactionDAO) { this.transactionDAO = transactionDAO; }
    public Map<String, BigDecimal> monthSummary(YearMonth month) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        for (FinanceTransaction tx : transactionDAO.findAll()) {
            if (!YearMonth.from(tx.getDate()).equals(month)) continue;
            if (tx.getType() == TransactionType.INCOME) income = income.add(tx.getAmount());
            else if (tx.getType() == TransactionType.EXPENSE) expense = expense.add(tx.getAmount());
        }
        Map<String, BigDecimal> summary = new LinkedHashMap<>();
        summary.put("income", income); summary.put("expense", expense); summary.put("net", income.subtract(expense));
        return summary;
    }
}
