package com.kharchenko.university.service;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;

import java.util.List;

public interface StudentService extends GenericService<Student, Integer> {

    List<Student> getGroupStudents(Group group);
}
