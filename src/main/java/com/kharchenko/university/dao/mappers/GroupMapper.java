package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Subject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("group_id");
        String name = resultSet.getString("group_name");
        Integer facultyId = resultSet.getInt("faculty_id");
        String facultyName = resultSet.getString("faculty_name");
        Faculty faculty = new Faculty(facultyId, facultyName);
        List<Subject> subjects = new ArrayList<>();
        do {
            Integer subjectId = resultSet.getInt("subject_id");
            String subjectName = resultSet.getString("subject_name");
            String subjectDescription = resultSet.getString("subject_description");
            if (subjectId != null && subjectName != null && subjectDescription != null) {
                subjects.add(new Subject(subjectId, subjectName, subjectDescription));
            }
        } while (resultSet.next() && id == resultSet.getInt("group_id"));
        resultSet.previous();
        return new Group(id, name, subjects, faculty);
    }
}