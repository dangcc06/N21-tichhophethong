package com.example.finance;

public record TuitionStatistics(
        long totalRecords,
        long unpaid,
        long partiallyPaid,
        long paid) {
}
