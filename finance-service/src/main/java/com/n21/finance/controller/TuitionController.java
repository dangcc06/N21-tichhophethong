package com.n21.finance.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.n21.finance.dto.CreateTuitionRequest;
import com.n21.finance.dto.TuitionStatistics;
import com.n21.finance.dto.UpdatePaidAmountRequest;
import com.n21.finance.model.Tuition;
import com.n21.finance.service.TuitionService;

@RestController
@RequestMapping("/api/tuition")
public class TuitionController {

    private final TuitionService tuitionService;

    public TuitionController(TuitionService tuitionService) {
        this.tuitionService = tuitionService;
    }

    @GetMapping
    public List<Tuition> findAll() {
        return tuitionService.findAll();
    }

    @GetMapping("/statistics")
    public TuitionStatistics getStatistics() {
        return tuitionService.getStatistics();
    }

    @GetMapping("/{studentId}")
    public Tuition findByStudentId(@PathVariable String studentId) {
        return tuitionService.findByStudentId(studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tuition create(@Valid @RequestBody CreateTuitionRequest request) {
        return tuitionService.create(request);
    }

    @PutMapping("/{studentId}")
    public Tuition updatePaidAmount(
            @PathVariable String studentId,
            @Valid @RequestBody UpdatePaidAmountRequest request) {
        return tuitionService.updatePaidAmount(studentId, request.paidAmount());
    }

    @DeleteMapping("/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String studentId) {
        tuitionService.delete(studentId);
    }
}
