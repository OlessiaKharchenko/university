package com.kharchenko.university.model.dto;

import com.kharchenko.university.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private Integer id;
    private String name;
    private List<Subject> subjects;
    private int facultyId;
}

