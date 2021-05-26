package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Subject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubjectMapper implements RowMapper<Subject> {

    @Override
    public Subject mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("subject_id");
        String name = resultSet.getString("subject_name");
        String description = resultSet.getString("subject_description");
        return new Subject(id, name, description);
    }
}
