package com.kharchenko.university.service;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;

public interface SubjectService extends GenericService<Subject, Integer> {

    void addTeacherToSubject(Subject subject, Teacher teacher);

    void addSubjectToGroup(Subject subject, Group group);
}

