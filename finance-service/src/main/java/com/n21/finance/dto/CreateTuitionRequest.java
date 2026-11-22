package com.n21.finance.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTuitionRequest(
        @NotBlank @Size(max = 100) String studentId,
        @NotNull @DecimalMin(value = "0.01", message = "tuitionAmount must be greater than zero")
        BigDecimal tuitionAmount,
        @NotNull @DecimalMin(value = "0.00", message = "paidAmount cannot be negative")
        BigDecimal paidAmount) {
}
