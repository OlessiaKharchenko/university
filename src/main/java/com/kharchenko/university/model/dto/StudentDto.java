package com.kharchenko.university.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private int groupId;
}
