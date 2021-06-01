package com.kharchenko.university.service;

import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidClassRoomException;
import com.kharchenko.university.exception.InvalidGroupException;
import com.kharchenko.university.exception.InvalidTeacherException;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Group;

import java.util.List;

public interface LectureService extends GenericService<Lecture, Integer> {

    void addLectureToSchedule(Lecture lecture, Schedule schedule) throws InvalidTeacherException,
            InvalidClassRoomException, InvalidGroupException;

    void removeLectureFromSchedule(Lecture lecture, Schedule schedule);

    List<Lecture> getByClassRoom(ClassRoom classRoom) throws EntityNotFoundException;

    List<Lecture> getBySubject(Subject subject) throws EntityNotFoundException;

    List<Lecture> getTeacherLectures(Teacher teacher) throws EntityNotFoundException;

    List<Lecture> getGroupLectures(Group group) throws EntityNotFoundException;
}