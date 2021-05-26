package com.kharchenko.university.dao;

import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;

import java.util.List;

public interface TeacherDao extends GenericDao<Teacher, Integer> {

    List<Teacher> getBySubject(Subject subject);
}
