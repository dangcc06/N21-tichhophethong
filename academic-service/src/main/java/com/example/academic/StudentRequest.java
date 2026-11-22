package com.example.academic;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record StudentRequest(
    @Pattern(regexp = Student.ID_PATTERN, message = Student.INVALID_ID_MESSAGE)
    String studentId,

    @NotBlank(message = "Họ tên là bắt buộc.")
    @Size(max = 150, message = "Họ tên tối đa 150 ký tự.")
    String fullName,

    @Email(message = "Email không hợp lệ.")
    @Size(max = 254, message = "Email tối đa 254 ký tự.")
    String email,

    @NotBlank(message = "Ngành học là bắt buộc.")
    @Size(max = 150, message = "Ngành học tối đa 150 ký tự.")
    String major,

    @NotBlank(message = "Học kỳ hiện tại là bắt buộc.")
    @Size(max = 50, message = "Học kỳ hiện tại tối đa 50 ký tự.")
    String currentSemester
) {
    Student toStudent(String id) {
        return new Student(id, fullName.strip(), email, major.strip(), currentSemester.strip());
    }
}
