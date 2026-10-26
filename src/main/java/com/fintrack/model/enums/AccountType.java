package com.fintrack.model.enums;

public enum AccountType {
    CASH, BANK, CREDIT_CARD, WALLET;

    @Override
    public String toString() {
        return switch (this) {
            case CASH -> "Cash";
            case BANK -> "Bank account";
            case CREDIT_CARD -> "Credit card";
            case WALLET -> "Digital wallet";
        };
    }
}
