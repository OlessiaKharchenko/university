package com.kharchenko.university.dao;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;

import java.util.List;

public interface StudentDao extends GenericDao<Student, Integer> {

    List<Student> getGroupStudents(Group group);
}