package com.fintrack.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private DateUtils() { }
    public static LocalDate parseDate(String value) { return LocalDate.parse(value.trim(), ISO_DATE); }
    public static YearMonth parseMonth(String value) { return YearMonth.parse(value.trim()); }
    public static boolean isValidDate(String value) {
        try { parseDate(value); return true; } catch (DateTimeParseException | NullPointerException ex) { return false; }
    }
}
