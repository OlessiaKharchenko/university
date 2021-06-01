package com.kharchenko.university.service;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.Teacher;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService extends GenericService<Schedule, Integer> {

    List<Schedule> getByLecture(Lecture lecture);

    List<Schedule> getByFaculty(Faculty faculty);

    void changeTeacher(Teacher teacher, LocalDate fromDate, LocalDate toDate);
}