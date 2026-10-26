package com.fintrack.service;

import com.fintrack.dao.BudgetDAO;
import com.fintrack.dao.TransactionDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.Budget;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BudgetService {
    private final BudgetDAO budgetDAO;
    private final TransactionDAO transactionDAO;
    public BudgetService(BudgetDAO budgetDAO, TransactionDAO transactionDAO) { this.budgetDAO = budgetDAO; this.transactionDAO = transactionDAO; }
    public List<Budget> findAll() { return budgetDAO.findAll(); }
    public Optional<Budget> findById(long id) { return budgetDAO.findById(id); }
    public Budget create(Budget budget) { validate(budget); budget.setId(null); return budgetDAO.save(budget); }
    public Budget update(Budget budget) { validate(budget); if(budget.getId()==null) throw new ValidationException("Budget ID is required for update."); return budgetDAO.save(budget); }
    public void deleteById(long id) { if(id<=0) throw new ValidationException("Budget ID must be positive."); budgetDAO.deleteById(id); }
    public BigDecimal spentFor(Budget budget) {
        return transactionDAO.findAll().stream()
            .filter(tx -> tx.getType() == TransactionType.EXPENSE)
            .filter(tx -> tx.getCategoryId() != null && tx.getCategoryId() == budget.getCategoryId())
            .filter(tx -> java.time.YearMonth.from(tx.getDate()).equals(budget.getMonth()))
            .map(FinanceTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void validate(Budget budget) {
        if(budget==null) throw new ValidationException("Budget is required.");
        if(budget.getCategoryId()<=0) throw new ValidationException("Select a category for the budget.");
        if(budget.getMonth()==null) throw new ValidationException("Budget month is required.");
        if(budget.getLimitAmount()==null || budget.getLimitAmount().compareTo(BigDecimal.ZERO)<=0) throw new ValidationException("Budget limit must be greater than zero.");
        if(budget.getLimitAmount().scale()>2) budget.setLimitAmount(budget.getLimitAmount().setScale(2, java.math.RoundingMode.HALF_UP));
    }
}
