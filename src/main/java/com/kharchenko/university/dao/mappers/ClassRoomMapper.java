package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassRoomMapper implements RowMapper<ClassRoom> {

    @Override
    public ClassRoom mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("classroom_id");
        int buildingNumber = resultSet.getInt("building_number");
        int roomNumber = resultSet.getInt("room_number");
        Integer facultyId = resultSet.getInt("faculty_id");
        String facultyName = resultSet.getString("faculty_name");
        Faculty faculty = new Faculty(facultyId, facultyName);
        return new ClassRoom(id, buildingNumber, roomNumber, faculty);
    }
}