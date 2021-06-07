package com.kharchenko.university.service;

import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.Lecture;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TimeTableTest {

    private TimeTable timeTable = new TimeTable(TestData.getSchedules());

    @Test
    void getTeacherMonthSchedule_shouldReturnNumberWorkingDays() {
        Teacher teacher = TestData.getTeacher();
        final Month month = LocalDate.of(2021, 4, 1).getMonth();
        long expected = 2;
        long actual = timeTable.getTeacherMonthSchedule(teacher, month).stream()
                .filter(schedule -> !schedule.getLectures().isEmpty()).count();
        assertEquals(expected, actual);
    }

    @Test
    void getTeacherMonthSchedule_shouldReturnCorrectTeacher() {
        Teacher teacher = TestData.getTeacher();
        final Month month = LocalDate.of(2021, 4, 1).getMonth();
        boolean isCorrectFirstAndLastName = timeTable.getTeacherMonthSchedule(teacher, month).stream()
                .map(Schedule::getLectures)
                .flatMap(Collection::stream)
                .allMatch(lecture -> lecture.getTeacher().getFirstName().equals("Ivan") &&
                        lecture.getTeacher().getLastName().equals("Ivanov"));
        assertTrue(isCorrectFirstAndLastName);
    }

    @Test
    void getStudentMonthSchedule_shouldReturnNumberStudyingDays() {
        Student student = TestData.getFirstStudent();
        final Month month = LocalDate.of(2021, 4, 1).getMonth();
        long expected = 2;
        long actual = timeTable.getStudentMonthSchedule(student, month).stream()
                .filter(schedule -> !schedule.getLectures().isEmpty()).count();
        assertEquals(expected, actual);
    }

    @Test
    void getTeacherDaySchedule_shouldReturnCorrectLecturesNumber() {
        Teacher teacher = TestData.getTeacher();
        long expected = 1;
        long actual = timeTable.getTeacherDaySchedule(teacher, LocalDate.of(2021, 4, 2))
                .getLectures().size();
        assertEquals(expected, actual);
    }

    @Test
    void getTeacherDaySchedule_shouldReturnLecturesEndTime() {
        Teacher teacher = TestData.getTeacher();
        LocalTime expected = LocalTime.of(14, 00);
        List<Lecture> lectures = timeTable.getTeacherDaySchedule(teacher, LocalDate.of(2021, 4, 1))
                .getLectures();
        LocalTime actual = lectures.get(lectures.size() - 1).getEndTime();
        assertEquals(expected, actual);
    }

    @Test
    void getTeacherDaySchedule_shouldReturnCorrectSubjectNames() {
        Teacher teacher = TestData.getTeacher();
        List<String> expected = Arrays.asList("Java", "SQL");
        List<String> actual = timeTable.getTeacherDaySchedule(teacher, LocalDate.of(2021, 4, 1))
                .getLectures().stream()
                .map(lecture -> lecture.getSubject().getName())
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    void getStudentDaySchedule_shouldReturnCorrectLecturesNumber() {
        Student student = TestData.getFirstStudent();
        long expected = 2;
        long actual = timeTable.getStudentDaySchedule(student, LocalDate.of(2021, 4, 1))
                .getLectures().size();
        assertEquals(expected, actual);
    }

    @Test
    void getStudentDaySchedule_shouldReturnLecturesEndTime() {
        Student student = TestData.getFirstStudent();
        LocalTime expected = LocalTime.of(11, 00);
        List<Lecture> lectures = timeTable.getStudentDaySchedule(student, LocalDate.of(2021, 4, 2))
                .getLectures();
        LocalTime actual = lectures.get(lectures.size() - 1).getEndTime();
        assertEquals(expected, actual);
    }

    @Test
    void getStudentDaySchedule_shouldReturnCorrectSubjectNames() {
        Student student = TestData.getFirstStudent();
        List<String> expected = Arrays.asList("Java");
        List<String> actual = timeTable.getStudentDaySchedule(student, LocalDate.of(2021, 4, 2))
                .getLectures().stream()
                .map(lecture -> lecture.getSubject().getName())
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }
}