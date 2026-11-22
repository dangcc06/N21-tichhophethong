package com.example.academic;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("students")
public record Student(
    @MongoId String studentId,
    String fullName,
    String email,
    String major,
    String currentSemester
) {
    static final String ID_PATTERN = "^[A-Za-z0-9_-]{1,20}$";
    static final String INVALID_ID_MESSAGE = "Mã sinh viên phải có từ 1 đến 20 ký tự là chữ cái ASCII, chữ số, dấu gạch dưới hoặc dấu gạch ngang.";
}
