package com.kharchenko.university.dao.mappers;

import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.Subject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet resultSet, int i) throws SQLException {
        Integer id = resultSet.getInt("student_id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        Integer groupId = resultSet.getInt("group_id");
        String groupName = resultSet.getString("group_name");
        Integer facultyId = resultSet.getInt("faculty_id");
        String facultyName = resultSet.getString("faculty_name");
        List<Subject> subjects = new ArrayList<>();
        do {
            Integer subjectId = resultSet.getInt("subject_id");
            String subjectName = resultSet.getString("subject_name");
            String subjectDescription = resultSet.getString("subject_description");
            subjects.add(new Subject(subjectId, subjectName, subjectDescription));
        } while (resultSet.next() && id == resultSet.getInt("student_id"));
        resultSet.previous();
        Faculty faculty = new Faculty(facultyId, facultyName);
        Group group = new Group(groupId, groupName, subjects, faculty);
        return new Student(id, firstName, lastName, group);
    }
}
