package com.kharchenko.university.service;

import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Group;

import java.util.List;

public interface LectureService extends GenericService<Lecture, Integer> {

    void addLectureToSchedule(Lecture lecture, Schedule schedule);

    void removeLectureFromSchedule(Lecture lecture, Schedule schedule);

    List<Lecture> getByClassRoom(ClassRoom classRoom);

    List<Lecture> getBySubject(Subject subject);

    List<Lecture> getTeacherLectures(Teacher teacher);

    List<Lecture> getGroupLectures(Group group);
}
