package com.fintrack.dao;

import com.fintrack.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountDAO {
    List<Account> findAll();
    Optional<Account> findById(long id);
    Account save(Account value);
    void deleteById(long id);
}
