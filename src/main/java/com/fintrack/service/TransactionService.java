package com.fintrack.service;

import com.fintrack.dao.TransactionDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.util.InputValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private final TransactionDAO dao;
    public TransactionService(TransactionDAO dao) { this.dao = dao; }
    public List<FinanceTransaction> findAll() { return dao.findAll(); }
    public Optional<FinanceTransaction> findById(long id) { return dao.findById(id); }
    public FinanceTransaction create(FinanceTransaction tx) { validate(tx); tx.setId(null); return dao.save(tx); }
    public FinanceTransaction update(FinanceTransaction tx) {
        if (tx == null || tx.getId() == null) throw new ValidationException("An existing transaction ID is required for an update.");
        validate(tx); return dao.save(tx);
    }
    public void deleteById(long id) { if (id <= 0) throw new ValidationException("Transaction ID must be positive."); dao.deleteById(id); }
    private void validate(FinanceTransaction tx) {
        if (tx == null) throw new ValidationException("Transaction is required.");
        if (!InputValidator.isNonBlank(tx.getDescription())) throw new ValidationException("Description is required.");
        if (!InputValidator.isPositiveAmount(tx.getAmount())) throw new ValidationException("Amount must be greater than zero.");
        if (!InputValidator.isValidDate(tx.getDate()) || tx.getDate().isAfter(LocalDate.now().plusYears(100))) throw new ValidationException("A valid transaction date is required.");
        if (tx.getType() == null) throw new ValidationException("Transaction type is required.");
        if (tx.getAccountId() == null || tx.getAccountId() <= 0) throw new ValidationException("Select an account.");
    }
}
