package com.kharchenko.university.dao;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Faculty;

import java.util.List;

public interface GroupDao extends GenericDao<Group, Integer> {

    void addLectureToGroup(Lecture lecture, Group group);

    void removeLectureFromGroup(Lecture lecture, Group group);

    List<Group> getBySubject(Subject subject);

    List<Group> getByFaculty(Faculty faculty);
}
