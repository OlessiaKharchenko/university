package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;
    @Autowired
    private LectureDao lectureDao;
    @Autowired
    private TeacherDao teacherDao;

    @Override
    public void changeTeacher(Teacher teacher, LocalDate fromDate, LocalDate toDate) {
        List<Teacher> teachers = teacherDao.getAll();
        for (Schedule schedule : getSchedulesByPeriod(fromDate, toDate)) {
            for (Teacher otherTeacher : teachers) {
                for (Lecture lecture : schedule.getLectures()) {
                    if (lecture.getTeacher().equals(teacher) && otherTeacher.getSubjects().contains(lecture.getSubject())
                            && isOtherTeacherFree(schedule, lecture, otherTeacher)) {
                        lecture.setTeacher(otherTeacher);
                    }
                }
            }
        }
    }

    @Override
    public Schedule add(Schedule schedule) throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        validateSchedule(schedule);
        return scheduleDao.add(schedule);
    }

    @Override
    public List<Schedule> getAll() {
        return getWithAllFields(scheduleDao.getAll());
    }

    @Override
    public Schedule getById(Integer id) throws EntityNotFoundException {
        Schedule schedule = scheduleDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("The schedule doesn't exist with id " + id));
        setLectureFields(schedule);
        return schedule;
    }

    @Override
    public void update(Schedule schedule) throws EntityIsAlreadyExistsException, EntityNotFoundException, InvalidEntityFieldException {
        if (schedule.getId() == null) {
            throw new EntityNotFoundException("The schedule doesn't exist with id " + schedule.getId());
        }
        validateSchedule(schedule);
        scheduleDao.update(schedule);
    }

    @Override
    public boolean deleteById(Integer id) throws EntityNotFoundException, EntityHasReferenceException {
        if (hasLectures(getById(id))) {
            throw new EntityHasReferenceException("Schedule with id " + id + " has lectures.");
        }
        return scheduleDao.deleteById(id);
    }

    @Override
    public void addAll(List<Schedule> schedules) throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        for (Schedule schedule : schedules) {
            validateSchedule(schedule);
        }
        scheduleDao.addAll(schedules);
    }

    @Override
    public List<Schedule> getByLecture(Lecture lecture) {
        return getWithAllFields(scheduleDao.getByLecture(lecture));
    }

    @Override
    public List<Schedule> getByFaculty(Faculty faculty) {
        return getWithAllFields(scheduleDao.getByFaculty(faculty));
    }

    private void setLectureFields(Schedule schedule) {
        schedule.setLectures(schedule.getLectures().stream()
                .map(lecture -> lecture = lectureDao.getById(lecture.getId()).get())
                .collect(Collectors.toList()));
    }

    private List<Schedule> getWithAllFields(List<Schedule> schedules) {
        return schedules.stream().peek(this::setLectureFields).collect(Collectors.toList());
    }

    private boolean hasLectures(Schedule schedule) {
        return !schedule.getLectures().isEmpty();
    }

    private void validateSchedule(Schedule schedule) throws InvalidEntityFieldException, EntityIsAlreadyExistsException {
        validateScheduleFields(schedule);
        checkIfUnique(schedule);
    }

    private void validateScheduleFields(Schedule schedule) throws InvalidEntityFieldException {
        if (schedule.getDate() == null) {
            throw new InvalidEntityFieldException("Schedule's date can't be null");
        }
        if (schedule.getFaculty() == null) {
            throw new InvalidEntityFieldException("Schedule's faculty can't be null");
        }
        if (schedule.getDate().getDayOfWeek() == DayOfWeek.SATURDAY || schedule.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new InvalidEntityFieldException("Lectures mustn't be on the weekend");
        }
        if (schedule.getLectures() != null) {
            validateLectures(schedule);
        }
    }

    private void validateLectures(Schedule schedule) throws InvalidEntityFieldException {
        if (schedule.getLectures().stream()
                .noneMatch(lecture -> lecture.getClassRoom().getFaculty().equals(schedule.getFaculty()))) {
            throw new InvalidEntityFieldException("The faculty's schedule must include only faculty's classrooms");
        }
        for (Lecture lecture : schedule.getLectures()) {
            for (Group group : lecture.getGroups()) {
                if (!group.getFaculty().equals(schedule.getFaculty())) {
                    throw new InvalidEntityFieldException("The faculty's schedule must include only faculty's groups");
                }
            }
        }
    }

    private void checkIfUnique(Schedule schedule) throws EntityIsAlreadyExistsException {
        if (scheduleDao.getAll().stream().map(Schedule::getDate)
                .anyMatch(date -> date.isEqual(schedule.getDate()))) {
            throw new EntityIsAlreadyExistsException("Schedule with date " + schedule.getDate().toString() + " is already exists");
        }
    }

    private List<Schedule> getSchedulesByPeriod(LocalDate fromDate, LocalDate toDate) {
        return getAll().stream()
                .filter(schedule -> !schedule.getDate().isBefore(fromDate) && !schedule.getDate().isAfter(toDate))
                .collect(Collectors.toList());
    }

    private boolean isOtherTeacherFree(Schedule schedule, Lecture lecture, Teacher teacher) {
        return schedule.getLectures().stream()
                .filter(l -> l.getTeacher().equals(teacher))
                .noneMatch(l -> l.getStartTime().equals(lecture.getStartTime()) && l.getEndTime().equals(lecture.getEndTime()));
    }
}