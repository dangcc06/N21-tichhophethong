package com.example.academic;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
class StudentController {

    private static final String STUDENTS_PATH = "/api/students";
    private static final String STUDENT_PATH = STUDENTS_PATH + "/{id}";

    private final StudentService service;

    StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping(STUDENTS_PATH)
    List<Student> findAll() {
        return service.findAll();
    }

    @GetMapping({STUDENT_PATH, "/api/academic/students/{id}"})
    Student findById(@PathVariable("id") String id) {
        return service.findById(validatedId(id));
    }

    @PostMapping(STUDENTS_PATH)
    ResponseEntity<Student> create(@Valid @RequestBody StudentRequest request, UriComponentsBuilder uriBuilder) {
        Student student = service.create(request);
        return ResponseEntity.created(uriBuilder.path(STUDENT_PATH).buildAndExpand(student.studentId()).toUri())
            .body(student);
    }

    @PutMapping(STUDENT_PATH)
    Student update(
        @PathVariable("id") String id,
        @Valid @RequestBody StudentRequest request
    ) {
        return service.update(validatedId(id), request);
    }

    @DeleteMapping(STUDENT_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") String id) {
        service.delete(validatedId(id));
    }

    private String validatedId(String id) {
        if (!id.matches(Student.ID_PATTERN)) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "INVALID_STUDENT_ID", Student.INVALID_ID_MESSAGE);
        }
        return id;
    }
}
