package com.kharchenko.university.service;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;

import java.util.List;

public interface GroupService extends GenericService<Group, Integer> {

    void addLectureToGroup(Lecture lecture, Group group);

    List<Group> getBySubject(Subject subject);

    List<Group> getByLecture(Lecture lecture);

    List<Group> getByFaculty(Faculty faculty);
}

