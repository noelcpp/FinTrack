package com.fintrack.model;

import com.fintrack.model.enums.AccountType;
import java.math.BigDecimal;

public class Account {
    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal openingBalance;

    public Account(Long id, String name, AccountType type, BigDecimal openingBalance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.openingBalance = openingBalance;
    }
    public Account(String name, AccountType type, BigDecimal openingBalance) {
        this(null, name, type, openingBalance);
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
    @Override public String toString() { return name + " (" + type + ")"; }
}
