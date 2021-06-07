package com.kharchenko.university.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.Teacher;

@Data
@AllArgsConstructor
public class TimeTable {
    private List<Schedule> schedules;

    public Schedule getTeacherDaySchedule(Teacher teacher, LocalDate date) {
        return getDaySchedule(date).get().getLectures().stream()
                .filter(lecture -> lecture.getTeacher().equals(teacher))
                .collect(scheduleCollector(date));
    }

    public Schedule getStudentDaySchedule(Student student, LocalDate date) {
        return getDaySchedule(date).get().getLectures().stream()
                .filter(lecture -> lecture.getGroups().contains(student.getGroup()))
                .collect(scheduleCollector(date));
    }

    public List<Schedule> getTeacherMonthSchedule(Teacher teacher, Month month) {
        return getMonthSchedule(month).stream()
                .map(schedule -> new Schedule((schedule.getLectures().stream()
                        .filter(lecture -> lecture.getTeacher().equals(teacher))
                        .collect(Collectors.toList())), schedule.getDate()))
                .collect(Collectors.toList());
    }

    public List<Schedule> getStudentMonthSchedule(Student student, Month month) {
        return getMonthSchedule(month).stream()
                .map(schedule -> new Schedule((schedule.getLectures().stream()
                        .filter(lecture -> lecture.getGroups().contains(student.getGroup()))
                        .collect(Collectors.toList())), schedule.getDate()))
                .collect(Collectors.toList());
    }

    private Optional<Schedule> getDaySchedule(LocalDate date) {
        return schedules.stream()
                .filter(s -> s.getDate().equals(date))
                .findFirst();
    }

    private List<Schedule> getMonthSchedule(Month month) {
        return schedules.stream()
                .filter(schedule -> schedule.getDate().getMonth().equals(month))
                .collect(Collectors.toList());
    }

    private static Collector<Lecture, List<Lecture>, Schedule> scheduleCollector(LocalDate date) {
        return Collector.of(
                () -> new ArrayList<Lecture>(),
                (list, lecture) -> list.add(lecture),
                (firstList, secondList) -> {
                    firstList.addAll(secondList);
                    return firstList;
                },
                firstList -> new Schedule(firstList, date));
    }
}