package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.StudentDao;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Override
    public Student add(Student student) throws InvalidEntityFieldException {
        validateStudentFields(student);
        return studentDao.add(student);
    }

    @Override
    public List<Student> getAll() {
        return studentDao.getAll();
    }

    @Override
    public Student getById(Integer id) throws EntityNotFoundException {
        return studentDao.getById(id).orElseThrow(() -> new EntityNotFoundException("Student doesn't exist with id " + id));
    }

    @Override
    public void update(Student student) throws EntityNotFoundException, InvalidEntityFieldException {
        if (student.getId() == null) {
            throw new EntityNotFoundException("Student doesn't exist with id " + student.getId());
        }
        validateStudentFields(student);
        studentDao.update(student);
    }

    @Override
    public boolean deleteById(Integer id) throws EntityNotFoundException {
        Student student = getById(id);
        return studentDao.deleteById(student.getId());
    }

    @Override
    public void addAll(List<Student> students) throws InvalidEntityFieldException {
        for (Student student : students) {
            validateStudentFields(student);
        }
        studentDao.addAll(students);
    }

    @Override
    public List<Student> getGroupStudents(Group group) {
        return studentDao.getGroupStudents(group);
    }

    private void validateStudentFields(Student student) throws InvalidEntityFieldException {
        if (student.getFirstName() == null || student.getFirstName().isEmpty()) {
            throw new InvalidEntityFieldException("Student's first name can't be empty or null");
        }
        if (student.getLastName() == null || student.getLastName().isEmpty()) {
            throw new InvalidEntityFieldException("Student's last name can't be empty or null");
        }
        if (student.getGroup() == null) {
            throw new InvalidEntityFieldException("Student's group can't be null");
        }
    }
}