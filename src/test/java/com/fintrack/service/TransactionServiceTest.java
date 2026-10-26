package com.fintrack.service;

import com.fintrack.dao.TransactionDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    @Test void rejectsZeroOrNegativeAmount() {
        TransactionService service = new TransactionService(new MemoryTransactionDAO());
        FinanceTransaction tx = new FinanceTransaction("Lunch", BigDecimal.ZERO, LocalDate.now(), TransactionType.EXPENSE, 1L, 1L);
        assertThrows(ValidationException.class, () -> service.create(tx));
    }
    @Test void savesValidTransaction() {
        TransactionService service = new TransactionService(new MemoryTransactionDAO());
        FinanceTransaction tx = new FinanceTransaction("Salary", new BigDecimal("1000.00"), LocalDate.now(), TransactionType.INCOME, 5L, 1L);
        FinanceTransaction saved = service.create(tx);
        assertNotNull(saved.getId()); assertEquals(new BigDecimal("1000.00"), saved.getAmount());
    }
    private static final class MemoryTransactionDAO implements TransactionDAO {
        private final List<FinanceTransaction> values=new ArrayList<>(); private long next=1;
        public List<FinanceTransaction> findAll(){return new ArrayList<>(values);}
        public Optional<FinanceTransaction> findById(long id){return values.stream().filter(t->t.getId()==id).findFirst();}
        public FinanceTransaction save(FinanceTransaction t){if(t.getId()==null)t.setId(next++);values.removeIf(existing->existing.getId().equals(t.getId()));values.add(t);return t;}
        public void deleteById(long id){values.removeIf(t->t.getId()==id);}
    }
}
