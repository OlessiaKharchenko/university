package com.kharchenko.university.service;

import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;

import java.util.List;

public interface TeacherService extends GenericService<Teacher, Integer> {

    List<Teacher> getBySubject(Subject subject);
}
