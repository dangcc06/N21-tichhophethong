package com.example.finance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TuitionRepository extends JpaRepository<Tuition, Long> {

    boolean existsByStudentId(String studentId);

    java.util.Optional<Tuition> findByStudentId(String studentId);

    long countByStatus(TuitionStatus status);
}
