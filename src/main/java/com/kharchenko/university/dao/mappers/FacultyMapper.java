package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Faculty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FacultyMapper implements RowMapper<Faculty> {

    @Override
    public Faculty mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("faculty_id");
        String name = resultSet.getString("faculty_name");
        return new Faculty(id, name);
    }
}