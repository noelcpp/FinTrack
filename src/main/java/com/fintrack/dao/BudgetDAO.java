package com.fintrack.dao;

import com.fintrack.model.Budget;
import java.util.List;
import java.util.Optional;

public interface BudgetDAO {
    List<Budget> findAll();
    Optional<Budget> findById(long id);
    Budget save(Budget value);
    void deleteById(long id);
}
