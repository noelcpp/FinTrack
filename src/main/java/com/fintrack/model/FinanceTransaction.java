package com.fintrack.model;

import com.fintrack.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FinanceTransaction {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private TransactionType type;
    private Long categoryId;
    private Long accountId;

    public FinanceTransaction(Long id, String description, BigDecimal amount, LocalDate date,
                              TransactionType type, Long categoryId, Long accountId) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    public FinanceTransaction(String description, BigDecimal amount, LocalDate date,
                              TransactionType type, Long categoryId, Long accountId) {
        this(null, description, amount, date, type, categoryId, accountId);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}
