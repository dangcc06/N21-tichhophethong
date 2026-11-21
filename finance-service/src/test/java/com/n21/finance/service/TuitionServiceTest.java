package com.n21.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.n21.finance.dto.CreateTuitionRequest;
import com.n21.finance.exception.ResourceNotFoundException;
import com.n21.finance.model.Tuition;
import com.n21.finance.model.TuitionStatus;
import com.n21.finance.repository.TuitionRepository;

@ExtendWith(MockitoExtension.class)
class TuitionServiceTest {

    @Mock
    private TuitionRepository tuitionRepository;

    private TuitionService tuitionService;

    @BeforeEach
    void setUp() {
        tuitionService = new TuitionService(tuitionRepository);
    }

    @Test
    void createDerivesUnpaidStatus() {
        CreateTuitionRequest request = new CreateTuitionRequest(
                "SV001", new BigDecimal("1000"), BigDecimal.ZERO);
        when(tuitionRepository.existsByStudentId("SV001")).thenReturn(false);
        when(tuitionRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Tuition.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Tuition tuition = tuitionService.create(request);

        assertEquals(TuitionStatus.UNPAID, tuition.getStatus());
        assertEquals(new BigDecimal("1000"), tuition.getRemainingAmount());
    }

    @Test
    void updateRejectsPaymentAboveTuitionAmount() {
        Tuition existing = new Tuition("SV001", new BigDecimal("1000"), BigDecimal.ZERO);
        when(tuitionRepository.findByStudentId("SV001")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> tuitionService.updatePaidAmount("SV001", new BigDecimal("1000.01")));
    }

    @Test
    void findByStudentIdReportsMissingTuition() {
        when(tuitionRepository.findByStudentId("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tuitionService.findByStudentId("missing"));
        verify(tuitionRepository).findByStudentId("missing");
    }
}
