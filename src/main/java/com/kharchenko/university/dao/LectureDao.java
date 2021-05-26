package com.kharchenko.university.dao;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.ClassRoom;

import java.util.List;

public interface LectureDao extends GenericDao<Lecture, Integer> {

    void addLectureToSchedule(Lecture lecture, Schedule schedule);

    List<Lecture> getByClassRoom(ClassRoom classRoom);

    List<Lecture> getBySubject(Subject subject);

    List<Lecture> getTeacherLectures(Teacher teacher);

    List<Lecture> getGroupLectures(Group group);
}