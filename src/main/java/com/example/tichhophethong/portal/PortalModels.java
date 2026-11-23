package com.example.tichhophethong.portal;

import java.math.BigDecimal;
import java.time.Instant;

record StudentProfile(
    String studentId,
    String fullName,
    String email,
    String major
) {
}

record TuitionDetails(
    String studentId,
    BigDecimal outstandingAmount,
    String currency,
    String status
) {
}

record StudentSummary(
    StudentProfile student,
    TuitionDetails tuition
) {
}

record PortalHealth(String status) {
}

record ApiError(
    Instant timestamp,
    int status,
    String code,
    String message,
    String path
) {
}
