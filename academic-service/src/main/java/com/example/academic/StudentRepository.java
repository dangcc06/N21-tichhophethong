package com.example.academic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
class StudentRepository {

    private final MongoOperations mongo;

    StudentRepository(MongoOperations mongo) {
        this.mongo = mongo;
    }

    List<Student> findAll() {
        return mongo.find(new Query().with(Sort.by("studentId")), Student.class);
    }

    Optional<Student> findById(String studentId) {
        return Optional.ofNullable(mongo.findById(studentId, Student.class));
    }

    Student insert(Student student) {
        return mongo.insert(student);
    }

    Optional<Student> update(Student student) {
        Update update = new Update()
            .set("fullName", student.fullName())
            .set("email", student.email())
            .set("major", student.major())
            .set("currentSemester", student.currentSemester());

        Student updated = mongo.findAndModify(
            byId(student.studentId()), update, FindAndModifyOptions.options().returnNew(true), Student.class
        );
        return Optional.ofNullable(updated);
    }

    boolean deleteById(String studentId) {
        return mongo.remove(byId(studentId), Student.class).getDeletedCount() == 1;
    }

    private Query byId(String studentId) {
        return Query.query(Criteria.where("studentId").is(studentId));
    }
}
