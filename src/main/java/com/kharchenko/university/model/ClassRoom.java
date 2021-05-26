package com.kharchenko.university.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassRoom {
    private Integer id;
    private int buildingNumber;
    private int roomNumber;
    private Faculty faculty;
}
