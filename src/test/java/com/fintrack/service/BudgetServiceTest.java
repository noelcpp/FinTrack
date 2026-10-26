package com.fintrack.service;

import com.fintrack.dao.BudgetDAO;
import com.fintrack.dao.TransactionDAO;
import com.fintrack.model.Budget;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class BudgetServiceTest {
    @Test void totalsOnlyExpenseTransactionsInBudgetMonthAndCategory() {
        YearMonth month = YearMonth.of(2026, 10);
        TransactionDAO transactions = new TransactionDAO() {
            public List<FinanceTransaction> findAll() { return List.of(
                new FinanceTransaction(1L,"Groceries",new BigDecimal("250.00"),LocalDate.of(2026,10,5),TransactionType.EXPENSE,2L,1L),
                new FinanceTransaction(2L,"Bus",new BigDecimal("50.00"),LocalDate.of(2026,10,6),TransactionType.EXPENSE,3L,1L),
                new FinanceTransaction(3L,"Salary",new BigDecimal("5000.00"),LocalDate.of(2026,10,1),TransactionType.INCOME,2L,1L),
                new FinanceTransaction(4L,"Old expense",new BigDecimal("100.00"),LocalDate.of(2026,9,30),TransactionType.EXPENSE,2L,1L)); }
            public Optional<FinanceTransaction> findById(long id) { return Optional.empty(); }
            public FinanceTransaction save(FinanceTransaction tx) { return tx; }
            public void deleteById(long id) { }
        };
        BudgetDAO budgets = new BudgetDAO() {
            public List<Budget> findAll(){return List.of();} public Optional<Budget> findById(long id){return Optional.empty();}
            public Budget save(Budget b){return b;} public void deleteById(long id){}
        };
        Budget budget = new Budget(2L, month, new BigDecimal("500.00"));
        assertEquals(new BigDecimal("250.00"), new BudgetService(budgets,transactions).spentFor(budget));
    }
}
