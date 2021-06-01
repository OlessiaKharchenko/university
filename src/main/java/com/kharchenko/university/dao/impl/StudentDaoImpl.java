package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.StudentDao;
import com.kharchenko.university.dao.mappers.StudentMapper;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StudentDaoImpl extends AbstractDao<Student> implements StudentDao {

    @Autowired
    private GroupDaoImpl groupDao;

    protected StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new StudentMapper(), jdbcTemplate);
    }

    @Override
    public Student add(Student student) {
        String query = "INSERT INTO students (first_name, last_name, group_id) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"student_id"});
            fillRow(statement, student);
            return statement;
        }, keyHolder);
        student.setId(keyHolder.getKey().intValue());
        return student;
    }

    @Override
    public void update(Student student) {
        String query = "UPDATE students SET first_name = ?, last_name = ?, group_id = ? WHERE student_id = ?;";
        jdbcTemplate.update(query, student.getFirstName(), student.getLastName(), student.getGroup().getId(), student.getId());
    }

    @Override
    public List<Student> getGroupStudents(Group group) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetGroupStudents(), group.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getFirstName());
        statement.setString(2, student.getLastName());
        Group group = student.getGroup();
        if (group.getId() == null) {
            groupDao.add(group);
        }
        statement.setInt(3, group.getId());
    }


    @Override
    protected String getQueryToGetAll() {
        return "SELECT s.student_id, first_name, last_name, g.group_id, group_name, f.faculty_id, faculty_name, " +
                "sb.subject_id, subject_name, subject_description FROM students s LEFT JOIN groups g " +
                "ON g.group_id = s.group_id LEFT JOIN faculties f ON g.faculty_id = f.faculty_id " +
                "LEFT JOIN groups_subjects gs ON g.group_id = gs.group_id " +
                "LEFT JOIN subjects sb ON sb.subject_id = gs.subject_id ORDER BY s.student_id, sb.subject_id;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM students WHERE student_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT s.student_id, first_name, last_name, g.group_id, group_name, f.faculty_id, faculty_name, " +
                "sb.subject_id, subject_name, subject_description FROM students s LEFT JOIN groups g " +
                "ON g.group_id = s.group_id LEFT JOIN faculties f ON g.faculty_id = f.faculty_id " +
                "LEFT JOIN groups_subjects gs ON g.group_id = gs.group_id " +
                "LEFT JOIN subjects sb ON sb.subject_id = gs.subject_id WHERE s.student_id = ? " +
                "ORDER BY s.student_id, sb.subject_id;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO students (first_name, last_name, group_id) VALUES (?, ?, ?);";
    }

    private String getQueryToGetGroupStudents() {
        return "SELECT s.student_id, first_name, last_name, g.group_id, group_name, f.faculty_id, faculty_name, " +
                "sb.subject_id, subject_name, subject_description FROM students s LEFT JOIN groups g " +
                "ON g.group_id = s.group_id LEFT JOIN faculties f ON g.faculty_id = f.faculty_id " +
                "LEFT JOIN groups_subjects gs ON g.group_id = gs.group_id " +
                "LEFT JOIN subjects sb ON sb.subject_id = gs.subject_id WHERE g.group_id = ? ORDER BY s.student_id;";
    }
}