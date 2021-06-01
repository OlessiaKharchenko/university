package com.kharchenko.university.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private Integer id;
    private String name;
    private List<Subject> subjects;
    private Faculty faculty;
}