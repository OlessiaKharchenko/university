package com.kharchenko.university.model.dto;

import com.kharchenko.university.model.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ScheduleDto {
    private Integer id;
    private List<Lecture> lectures;
    private String date;
    private int facultyId;
}
