package com.n21.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.n21.finance.model.Tuition;
import com.n21.finance.model.TuitionStatus;

public interface TuitionRepository extends JpaRepository<Tuition, Long> {

    boolean existsByStudentId(String studentId);

    java.util.Optional<Tuition> findByStudentId(String studentId);

    long countByStatus(TuitionStatus status);
}
