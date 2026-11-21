package com.n21.finance.dto;

public record TuitionStatistics(
        long totalRecords,
        long unpaid,
        long partiallyPaid,
        long paid) {
}
