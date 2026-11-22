package com.n21.finance.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.n21.finance.dto.CreateTuitionRequest;
import com.n21.finance.dto.TuitionStatistics;
import com.n21.finance.exception.DuplicateResourceException;
import com.n21.finance.exception.ResourceNotFoundException;
import com.n21.finance.model.Tuition;
import com.n21.finance.model.TuitionStatus;
import com.n21.finance.repository.TuitionRepository;

@Service
public class TuitionService {

    private final TuitionRepository tuitionRepository;

    public TuitionService(TuitionRepository tuitionRepository) {
        this.tuitionRepository = tuitionRepository;
    }

    public Tuition create(CreateTuitionRequest request) {
        validatePaidAmount(request.paidAmount(), request.tuitionAmount());
        if (tuitionRepository.existsByStudentId(request.studentId())) {
            throw new DuplicateResourceException("Tuition already exists for student " + request.studentId());
        }
        try {
            return tuitionRepository.saveAndFlush(new Tuition(
                    request.studentId(), request.tuitionAmount(), request.paidAmount()));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("Tuition already exists for student " + request.studentId());
        }
    }

    public List<Tuition> findAll() {
        return tuitionRepository.findAll();
    }

    public Tuition findByStudentId(String studentId) {
        return tuitionRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tuition not found for student " + studentId));
    }

    public Tuition updatePaidAmount(String studentId, BigDecimal paidAmount) {
        Tuition tuition = findByStudentId(studentId);
        validatePaidAmount(paidAmount, tuition.getTuitionAmount());
        tuition.setPaidAmount(paidAmount);
        tuition.updateStatus();
        return tuitionRepository.save(tuition);
    }

    public void delete(String studentId) {
        Tuition tuition = findByStudentId(studentId);
        tuitionRepository.delete(tuition);
    }

    public TuitionStatistics getStatistics() {
        long unpaid = tuitionRepository.countByStatus(TuitionStatus.UNPAID);
        long partiallyPaid = tuitionRepository.countByStatus(TuitionStatus.PARTIALLY_PAID);
        long paid = tuitionRepository.countByStatus(TuitionStatus.PAID);
        return new TuitionStatistics(tuitionRepository.count(), unpaid, partiallyPaid, paid);
    }

    private void validatePaidAmount(BigDecimal paidAmount, BigDecimal tuitionAmount) {
        if (paidAmount.signum() < 0) {
            throw new IllegalArgumentException("paidAmount cannot be negative");
        }
        if (paidAmount.compareTo(tuitionAmount) > 0) {
            throw new IllegalArgumentException("paidAmount cannot exceed tuitionAmount");
        }
    }
}
