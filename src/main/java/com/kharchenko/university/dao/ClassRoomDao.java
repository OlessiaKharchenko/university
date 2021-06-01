package com.kharchenko.university.dao;

import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;

import java.util.List;

public interface ClassRoomDao extends GenericDao<ClassRoom, Integer> {

    List<ClassRoom> getByBuildingNumber(Integer number);

    List<ClassRoom> getByFaculty(Faculty faculty);
}