package com.example.academic;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class StudentExceptionHandler {

    @ExceptionHandler(StudentApiException.class)
    ResponseEntity<ApiError> handleStudentError(StudentApiException exception, HttpServletRequest request) {
        return error(exception.status(), exception.code(), exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleInvalidBody(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error(HttpStatus.BAD_REQUEST, "INVALID_STUDENT", "Dữ liệu sinh viên không hợp lệ.", request, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> handleUnreadableBody(HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_JSON", "Nội dung JSON không hợp lệ.", request, Map.of());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<ApiError> handleDuplicate(HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "STUDENT_ALREADY_EXISTS", "Mã sinh viên đã tồn tại.", request, Map.of());
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    ResponseEntity<ApiError> handleDatabaseUnavailable(HttpServletRequest request) {
        return error(
            HttpStatus.SERVICE_UNAVAILABLE, "ACADEMIC_DATABASE_UNAVAILABLE",
            "Tạm thời chưa truy cập được dữ liệu sinh viên.", request, Map.of()
        );
    }

    private ResponseEntity<ApiError> error(
        HttpStatus status, String code, String message, HttpServletRequest request, Map<String, String> fieldErrors
    ) {
        ApiError body = new ApiError(
            Instant.now(), status.value(), code, message, request.getRequestURI(), fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }

    record ApiError(
        Instant timestamp, int status, String code, String message, String path, Map<String, String> fieldErrors
    ) {
    }
}
