package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherMapper implements RowMapper<Teacher> {

    @Override
    public Teacher mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("teacher_id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        List<Subject> subjects = new ArrayList<>();
        do {
            Integer subjectId = resultSet.getInt("subject_id");
            String name = resultSet.getString("subject_name");
            String description = resultSet.getString("subject_description");
            subjects.add(new Subject(subjectId, name, description));
        } while (resultSet.next() && id == resultSet.getInt("teacher_id"));
        resultSet.previous();
        return new Teacher(id, firstName, lastName, subjects);
    }
}
