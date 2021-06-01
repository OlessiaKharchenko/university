package com.kharchenko.university.model;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {
    private Integer id;
    private Subject subject;
    private Teacher teacher;
    private ClassRoom classRoom;
    private List<Group> groups;
    private LocalTime startTime;
    private LocalTime endTime;
}