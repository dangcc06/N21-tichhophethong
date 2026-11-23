package com.example.tichhophethong.portal;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class PortalService {

    private static final String STUDENT_ID_PATTERN = "^[A-Za-z0-9_-]{1,20}$";

    private final RestClient academicRestClient;
    private final RestClient financeRestClient;

    public PortalService(
        @Qualifier("academicRestClient") RestClient academicRestClient,
        @Qualifier("financeRestClient") RestClient financeRestClient
    ) {
        this.academicRestClient = academicRestClient;
        this.financeRestClient = financeRestClient;
    }

    public StudentSummary getStudentSummary(String studentId) {
        if (studentId == null || !studentId.matches(STUDENT_ID_PATTERN)) {
            throw new InvalidStudentIdException();
        }

        StudentProfile student = fetch(
            "Academic",
            studentId,
            () -> academicRestClient.get()
                .uri("/api/academic/students/{studentId}", studentId)
                .retrieve()
                .body(StudentProfile.class)
        );
        if (student == null) {
            throw new IntegrationFailureException(
                "Academic API returned an empty response"
            );
        }

        TuitionDetails tuition = fetch(
            "Finance",
            studentId,
            () -> financeRestClient.get()
                .uri("/api/finance/tuition/{studentId}", studentId)
                .retrieve()
                .body(TuitionDetails.class)
        );
        if (tuition == null) {
            throw new IntegrationFailureException(
                "Finance API returned an empty response"
            );
        }

        return new StudentSummary(student, tuition);
    }

    private <T> T fetch(
        String serviceName,
        String studentId,
        UpstreamRequest<T> request
    ) {
        try {
            return request.execute();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new StudentNotFoundException(studentId);
            }
            throw new IntegrationFailureException(serviceName, exception);
        } catch (ResourceAccessException exception) {
            if (isTimeout(exception)) {
                throw new UpstreamTimeoutException(serviceName, exception);
            }
            throw new IntegrationFailureException(serviceName, exception);
        } catch (RestClientException exception) {
            throw new IntegrationFailureException(serviceName, exception);
        }
    }

    private boolean isTimeout(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof SocketTimeoutException
                || current instanceof HttpTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @FunctionalInterface
    private interface UpstreamRequest<T> {
        T execute();
    }
}
