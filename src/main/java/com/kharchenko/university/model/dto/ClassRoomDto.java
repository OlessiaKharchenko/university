package com.kharchenko.university.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassRoomDto {
    private Integer id;
    private int buildingNumber;
    private int roomNumber;
    private int facultyId;
}
