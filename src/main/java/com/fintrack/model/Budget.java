package com.fintrack.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public class Budget {
    private Long id;
    private long categoryId;
    private YearMonth month;
    private BigDecimal limitAmount;

    public Budget(Long id, long categoryId, YearMonth month, BigDecimal limitAmount) {
        this.id = id;
        this.categoryId = categoryId;
        this.month = month;
        this.limitAmount = limitAmount;
    }
    public Budget(long categoryId, YearMonth month, BigDecimal limitAmount) {
        this(null, categoryId, month, limitAmount);
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public YearMonth getMonth() { return month; }
    public void setMonth(YearMonth month) { this.month = month; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }
}
