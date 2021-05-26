package com.kharchenko.university.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Schedule {
    private Integer id;
    private List<Lecture> lectures;
    private LocalDate date;
    private Faculty faculty;

    public Schedule(List<Lecture> lectures, LocalDate date) {
        this.lectures = lectures;
        this.date = date;
    }
}