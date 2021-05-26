package com.kharchenko.university.dao;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;

import java.util.List;

public interface ScheduleDao extends GenericDao<Schedule, Integer> {

    List<Schedule> getByLecture(Lecture lecture);

    List<Schedule> getByFaculty(Faculty faculty);
}
