package com.kharchenko.university.dao;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;

public interface SubjectDao extends GenericDao<Subject, Integer> {

    void addTeacherToSubject(Subject subject, Teacher teacher);

    void addSubjectToGroup(Subject subject, Group group);

    void removeSubjectFromGroup(Subject subject, Group group);

    void removeSubjectFromTeacher(Subject subject, Teacher teacher);
}
