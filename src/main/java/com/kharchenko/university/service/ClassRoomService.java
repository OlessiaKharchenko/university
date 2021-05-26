package com.kharchenko.university.service;

import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;

import java.util.List;

public interface ClassRoomService {

    List<ClassRoom> getByBuildingNumber(Integer number);

    List<ClassRoom> getByFaculty(Faculty faculty);
}

