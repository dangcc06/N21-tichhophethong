package com.example.finance;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UpdatePaidAmountRequest(
        @NotNull @DecimalMin(value = "0.00", message = "paidAmount cannot be negative")
        BigDecimal paidAmount) {
}
