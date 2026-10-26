package com.fintrack.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {
    @Test void rejectsBlankStrings() { assertFalse(InputValidator.isNonBlank("   ")); assertTrue(InputValidator.isNonBlank("Food")); }
    @Test void validatesPositiveAmounts() { assertTrue(InputValidator.isPositiveAmount(new BigDecimal("0.01"))); assertFalse(InputValidator.isPositiveAmount(BigDecimal.ZERO)); assertFalse(InputValidator.isPositiveAmount(new BigDecimal("-1"))); }
}
