package com.fintrack.util;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class InputValidator {
    private InputValidator() { }
    public static boolean isNonBlank(String value) { return value != null && !value.trim().isEmpty(); }
    public static boolean isPositiveAmount(BigDecimal value) { return value != null && value.compareTo(BigDecimal.ZERO) > 0; }
    public static boolean isValidDate(LocalDate value) { return value != null; }
}
