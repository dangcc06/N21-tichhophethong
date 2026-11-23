package com.example.academic;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
class StudentService {

    private final StudentRepository repository;

    StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    List<Student> findAll() {
        return repository.findAll();
    }

    Student findById(String studentId) {
        return repository.findById(studentId).orElseThrow(() -> notFound(studentId));
    }

    Student create(StudentRequest request) {
        if (request.studentId() == null) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "INVALID_STUDENT_ID", "Mã sinh viên là bắt buộc.");
        }
        return repository.insert(request.toStudent(request.studentId()));
    }

    Student update(String studentId, StudentRequest request) {
        if (request.studentId() != null && !studentId.equals(request.studentId())) {
            throw new StudentApiException(
                HttpStatus.BAD_REQUEST, "STUDENT_ID_MISMATCH", "Mã sinh viên trong JSON phải khớp với mã trên đường dẫn."
            );
        }
        return repository.update(request.toStudent(studentId)).orElseThrow(() -> notFound(studentId));
    }

    void delete(String studentId) {
        if (!repository.deleteById(studentId)) {
            throw notFound(studentId);
        }
    }

    private StudentApiException notFound(String studentId) {
        return new StudentApiException(
            HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", "Không tìm thấy sinh viên " + studentId + "."
        );
    }
}
