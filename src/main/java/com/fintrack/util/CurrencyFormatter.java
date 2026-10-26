package com.fintrack.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyFormatter {
    private static final NumberFormat INR = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
    private CurrencyFormatter() { }
    public static String format(BigDecimal amount) { return INR.format(amount == null ? BigDecimal.ZERO : amount); }
}
