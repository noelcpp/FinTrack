package com.fintrack.model.enums;

public enum TransactionType {
    INCOME, EXPENSE;

    @Override
    public String toString() {
        String lower = name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
