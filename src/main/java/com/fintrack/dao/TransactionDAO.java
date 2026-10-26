package com.fintrack.dao;

import com.fintrack.model.FinanceTransaction;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    List<FinanceTransaction> findAll();
    Optional<FinanceTransaction> findById(long id);
    FinanceTransaction save(FinanceTransaction value);
    void deleteById(long id);
}
