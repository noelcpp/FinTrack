package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.dao.TransactionDAO;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {
    @Test
    void calculatesInclusiveDateRangeAndSavingsRate() {
        MemoryTransactionDAO transactions = new MemoryTransactionDAO(List.of(
                tx(1L, "Salary", "2000.00", "2025-01-01", TransactionType.INCOME, 3L),
                tx(2L, "Groceries", "200.00", "2025-01-15", TransactionType.EXPENSE, 1L),
                tx(3L, "Electricity", "300.00", "2025-01-31", TransactionType.EXPENSE, 2L),
                tx(4L, "February purchase", "900.00", "2025-02-01", TransactionType.EXPENSE, 2L)
        ));
        MemoryCategoryDAO categories = new MemoryCategoryDAO(List.of(
                new Category(1L, "Food", TransactionType.EXPENSE),
                new Category(2L, "Bills", TransactionType.EXPENSE),
                new Category(3L, "Salary", TransactionType.INCOME)
        ));

        ReportService service = new ReportService(transactions, categories);
        ReportService.Summary summary = service.summary(LocalDate.parse("2025-01-01"), LocalDate.parse("2025-01-31"));

        assertEquals(new BigDecimal("2000.00"), summary.income());
        assertEquals(new BigDecimal("500.00"), summary.expenses());
        assertEquals(new BigDecimal("1500.00"), summary.netCashFlow());
        assertEquals(new BigDecimal("75.00"), summary.savingsRatePercent());
        assertEquals(3, summary.transactionCount());
    }

    @Test
    void ordersExpenseCategoriesByLargestSpendFirst() {
        MemoryTransactionDAO transactions = new MemoryTransactionDAO(List.of(
                tx(1L, "Groceries", "200.00", "2025-01-15", TransactionType.EXPENSE, 1L),
                tx(2L, "Electricity", "300.00", "2025-01-31", TransactionType.EXPENSE, 2L),
                tx(3L, "Salary", "2000.00", "2025-01-01", TransactionType.INCOME, 3L)
        ));
        MemoryCategoryDAO categories = new MemoryCategoryDAO(List.of(
                new Category(1L, "Food", TransactionType.EXPENSE),
                new Category(2L, "Bills", TransactionType.EXPENSE),
                new Category(3L, "Salary", TransactionType.INCOME)
        ));

        var result = new ReportService(transactions, categories)
                .spendingByCategory(LocalDate.parse("2025-01-01"), LocalDate.parse("2025-01-31"));

        assertEquals(List.of("Bills", "Food"), new ArrayList<>(result.keySet()));
    }

    private static FinanceTransaction tx(Long id, String description, String amount, String date,
                                          TransactionType type, Long categoryId) {
        return new FinanceTransaction(id, description, new BigDecimal(amount), LocalDate.parse(date), type, categoryId, 1L);
    }

    private static final class MemoryTransactionDAO implements TransactionDAO {
        private final List<FinanceTransaction> values;
        private MemoryTransactionDAO(List<FinanceTransaction> values) { this.values = new ArrayList<>(values); }
        @Override public List<FinanceTransaction> findAll() { return new ArrayList<>(values); }
        @Override public Optional<FinanceTransaction> findById(long id) { return values.stream().filter(v -> v.getId() == id).findFirst(); }
        @Override public FinanceTransaction save(FinanceTransaction value) { values.add(value); return value; }
        @Override public void deleteById(long id) { values.removeIf(v -> v.getId() != null && v.getId() == id); }
    }

    private static final class MemoryCategoryDAO implements CategoryDAO {
        private final List<Category> values;
        private MemoryCategoryDAO(List<Category> values) { this.values = new ArrayList<>(values); }
        @Override public List<Category> findAll() { return new ArrayList<>(values); }
        @Override public Optional<Category> findById(long id) { return values.stream().filter(v -> v.getId() != null && v.getId() == id).findFirst(); }
        @Override public Category save(Category value) { values.add(value); return value; }
        @Override public void deleteById(long id) { values.removeIf(v -> v.getId() != null && v.getId() == id); }
    }
}
