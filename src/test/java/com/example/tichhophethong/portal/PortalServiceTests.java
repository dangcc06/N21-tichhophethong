package com.example.tichhophethong.portal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PortalServiceTests {

    @Test
    void combinesAcademicAndFinanceResponses() {
        RestClient.Builder academicBuilder = RestClient.builder();
        RestClient.Builder financeBuilder = RestClient.builder();
        MockRestServiceServer academicServer =
            MockRestServiceServer.bindTo(academicBuilder).build();
        MockRestServiceServer financeServer =
            MockRestServiceServer.bindTo(financeBuilder).build();

        academicServer.expect(requestTo(
                "http://academic.test/api/academic/students/SV001"
            ))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                """
                {"studentId":"SV001","fullName":"Nguyen An",
                 "email":"an@example.edu","major":"Information Systems"}
                """,
                MediaType.APPLICATION_JSON
            ));
        financeServer.expect(requestTo(
                "http://finance.test/api/finance/tuition/SV001"
            ))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                """
                {"studentId":"SV001","outstandingAmount":2500000,
                 "currency":"VND","status":"UNPAID"}
                """,
                MediaType.APPLICATION_JSON
            ));

        PortalService service = new PortalService(
            academicBuilder.baseUrl("http://academic.test").build(),
            financeBuilder.baseUrl("http://finance.test").build()
        );

        StudentSummary result = service.getStudentSummary("SV001");

        assertEquals("Nguyen An", result.student().fullName());
        assertEquals(new BigDecimal("2500000"), result.tuition().outstandingAmount());
        assertEquals("UNPAID", result.tuition().status());
        academicServer.verify();
        financeServer.verify();
    }

    @Test
    void rejectsInvalidStudentIdBeforeCallingUpstreams() {
        PortalService service = serviceWithServers();

        assertThrows(
            InvalidStudentIdException.class,
            () -> service.getStudentSummary("bad.id")
        );
    }

    @Test
    void mapsAcademicNotFoundToStudentNotFound() {
        RestClient.Builder academicBuilder = RestClient.builder();
        RestClient.Builder financeBuilder = RestClient.builder();
        MockRestServiceServer academicServer =
            MockRestServiceServer.bindTo(academicBuilder).build();
        MockRestServiceServer.bindTo(financeBuilder).build();
        academicServer.expect(requestTo(
                "http://academic.test/api/academic/students/MISSING"
            ))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        PortalService service = new PortalService(
            academicBuilder.baseUrl("http://academic.test").build(),
            financeBuilder.baseUrl("http://finance.test").build()
        );

        assertThrows(
            StudentNotFoundException.class,
            () -> service.getStudentSummary("MISSING")
        );
        academicServer.verify();
    }

    @Test
    void mapsUpstreamTimeout() throws Exception {
        RestClient.Builder academicBuilder = RestClient.builder();
        RestClient.Builder financeBuilder = RestClient.builder();
        MockRestServiceServer academicServer =
            MockRestServiceServer.bindTo(academicBuilder).build();
        MockRestServiceServer.bindTo(financeBuilder).build();
        academicServer.expect(requestTo(
                "http://academic.test/api/academic/students/SV001"
            ))
            .andRespond(withException(new SocketTimeoutException("timed out")));

        PortalService service = new PortalService(
            academicBuilder.baseUrl("http://academic.test").build(),
            financeBuilder.baseUrl("http://finance.test").build()
        );

        assertThrows(
            UpstreamTimeoutException.class,
            () -> service.getStudentSummary("SV001")
        );
        academicServer.verify();
    }

    private PortalService serviceWithServers() {
        RestClient.Builder academicBuilder = RestClient.builder();
        RestClient.Builder financeBuilder = RestClient.builder();
        MockRestServiceServer.bindTo(academicBuilder).build();
        MockRestServiceServer.bindTo(financeBuilder).build();
        return new PortalService(
            academicBuilder.baseUrl("http://academic.test").build(),
            financeBuilder.baseUrl("http://finance.test").build()
        );
    }
}
