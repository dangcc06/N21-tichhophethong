package com.n21.finance.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tuition")
public class Tuition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 100)
    private String studentId;

    @Column(name = "tuition_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal tuitionAmount;

    @Column(name = "paid_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TuitionStatus status;

    public Tuition() {
    }

    public Tuition(String studentId, BigDecimal tuitionAmount, BigDecimal paidAmount) {
        this.studentId = studentId;
        this.tuitionAmount = tuitionAmount;
        this.paidAmount = paidAmount;
        updateStatus();
    }

    public void updateStatus() {
        if (paidAmount.signum() == 0) {
            status = TuitionStatus.UNPAID;
        } else if (paidAmount.compareTo(tuitionAmount) >= 0) {
            status = TuitionStatus.PAID;
        } else {
            status = TuitionStatus.PARTIALLY_PAID;
        }
    }

    public BigDecimal getRemainingAmount() {
        return tuitionAmount.subtract(paidAmount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getTuitionAmount() {
        return tuitionAmount;
    }

    public void setTuitionAmount(BigDecimal tuitionAmount) {
        this.tuitionAmount = tuitionAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public TuitionStatus getStatus() {
        return status;
    }

    public void setStatus(TuitionStatus status) {
        this.status = status;
    }
}
