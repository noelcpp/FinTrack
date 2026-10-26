package com.fintrack.service;

import com.fintrack.dao.AccountDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.Account;
import com.fintrack.util.InputValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {
    private final AccountDAO dao;
    public AccountService(AccountDAO dao) { this.dao = dao; }
    public List<Account> findAll() { return dao.findAll(); }
    public Optional<Account> findById(long id) { return dao.findById(id); }
    public Account create(Account account) { validate(account); account.setId(null); return dao.save(account); }
    public Account update(Account account) {
        validate(account); if(account.getId()==null) throw new ValidationException("Account ID is required for update."); return dao.save(account);
    }
    public void deleteById(long id) { if(id<=0) throw new ValidationException("Account ID must be positive."); dao.deleteById(id); }
    private void validate(Account account) {
        if(account==null || !InputValidator.isNonBlank(account.getName())) throw new ValidationException("Account name is required.");
        if(account.getName().trim().length()>60) throw new ValidationException("Account name must be 60 characters or fewer.");
        if(account.getType()==null) throw new ValidationException("Account type is required.");
        if(account.getOpeningBalance()==null) account.setOpeningBalance(BigDecimal.ZERO);
        if(account.getOpeningBalance().scale()>2) account.setOpeningBalance(account.getOpeningBalance().setScale(2, java.math.RoundingMode.HALF_UP));
        account.setName(account.getName().trim());
    }
}
