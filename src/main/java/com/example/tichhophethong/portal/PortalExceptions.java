package com.example.tichhophethong.portal;

class InvalidStudentIdException extends RuntimeException {
    InvalidStudentIdException() {
        super("studentId must contain 1-20 letters, digits, underscores, or hyphens");
    }
}

class StudentNotFoundException extends RuntimeException {
    StudentNotFoundException(String studentId) {
        super(
            "No student or tuition record was found for studentId " + studentId
        );
    }
}

class UpstreamTimeoutException extends RuntimeException {
    UpstreamTimeoutException(String serviceName, Throwable cause) {
        super(serviceName + " API timed out", cause);
    }
}

class IntegrationFailureException extends RuntimeException {
    IntegrationFailureException(String serviceName, Throwable cause) {
        super(serviceName + " API is unavailable or returned an error", cause);
    }

    IntegrationFailureException(String message) {
        super(message);
    }
}
