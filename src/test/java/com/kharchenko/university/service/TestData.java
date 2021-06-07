package com.kharchenko.university.service;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    private static final Faculty computerScience = new Faculty(1, "Computer Science");
    private static final Subject java = new Subject(1, "Java", "To learn java");
    private static final Subject sql = new Subject(2, "SQL", "To learn SQL");
    private static final List<Subject> computerScienceSubjects = getFirstSubjects();
    private static final Teacher teacher = new Teacher(1, "Ivan", "Ivanov", computerScienceSubjects);
    private static final Group groupOne = new Group(1, "A-11", computerScienceSubjects, computerScience);
    private static final Student firstStudent = new Student(1, "Maria", "Marchenko", groupOne);
    private static final Student secondStudent = new Student(2, "Yuriy", "Dehtiaruk", groupOne);
    private static final ClassRoom firstRoom = new ClassRoom(1, 1, 5, computerScience);
    private static final ClassRoom secondRoom = new ClassRoom(2, 1, 6, computerScience);
    private static final List<Group> javaGroups = getJavaGroups();
    private static final List<Group> sqlGroups = getSqlGroups();
    private static final Lecture javaLecture = new Lecture(1, java, teacher, firstRoom, javaGroups,
            LocalTime.of(9, 00), LocalTime.of(11, 00));
    private static final Lecture sqlLecture = new Lecture(1, sql, teacher, secondRoom, sqlGroups,
            LocalTime.of(11, 15), LocalTime.of(14, 00));
    private static final List<Lecture> oneDayLectures = getOneDayLectures();
    private static final List<Lecture> twoDayLectures = getTwoDayLectures();
    private static final Schedule scheduleOneDay = new Schedule(oneDayLectures, LocalDate.of(2021, 4, 1));
    private static final Schedule scheduleTwoDay = new Schedule(twoDayLectures, LocalDate.of(2021, 4, 2));

    public static Student getFirstStudent() {
        return firstStudent;
    }

    public static Teacher getTeacher() {
        return teacher;
    }

    public static List<Schedule> getSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(scheduleOneDay);
        schedules.add(scheduleTwoDay);
        return schedules;
    }

    public static List<Lecture> getTwoDayLectures() {
        List<Lecture> twoDayLectures = new ArrayList<>();
        twoDayLectures.add(javaLecture);
        return twoDayLectures;
    }

    public static List<Lecture> getOneDayLectures() {
        List<Lecture> oneDayLectures = new ArrayList<>();
        oneDayLectures.add(javaLecture);
        oneDayLectures.add(sqlLecture);
        return oneDayLectures;
    }

    public static List<Group> getJavaGroups() {
        List<Group> javaGroups = new ArrayList<>();
        javaGroups.add(groupOne);
        return javaGroups;
    }

    public static List<Group> getSqlGroups() {
        List<Group> sqlGroups = new ArrayList<>();
        sqlGroups.add(groupOne);
        return sqlGroups;
    }

    public static List<Subject> getFirstSubjects() {
        List<Subject> firstSubjects = new ArrayList<>();
        firstSubjects.add(java);
        firstSubjects.add(sql);
        return firstSubjects;
    }
}
