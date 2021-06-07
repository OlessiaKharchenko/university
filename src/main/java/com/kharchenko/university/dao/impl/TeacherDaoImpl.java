package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.dao.mappers.TeacherMapper;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
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
public class TeacherDaoImpl extends AbstractDao<Teacher> implements TeacherDao {

    @Autowired
    private SubjectDaoImpl subjectDao;

    protected TeacherDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new TeacherMapper(), jdbcTemplate);
    }

    @Override
    public Teacher add(Teacher teacher) {
        String query = "INSERT INTO teachers (first_name, last_name) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"teacher_id"});
            fillRow(statement, teacher);
            return statement;
        }, keyHolder);
        teacher.setId(keyHolder.getKey().intValue());
        addTeacherSubjects(teacher);
        return teacher;
    }

    @Override
    public void update(Teacher teacher) {
        String query = "UPDATE teachers SET first_name = ?, last_name = ? WHERE teacher_id = ?;";
        jdbcTemplate.update(query, teacher.getFirstName(), teacher.getLastName(), teacher.getId());
    }

    @Override
    public List<Teacher> getBySubject(Subject subject) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetBySubject(), subject.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Teacher teacher) throws SQLException {
        statement.setString(1, teacher.getFirstName());
        statement.setString(2, teacher.getLastName());
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT t.teacher_id, first_name, last_name, s.subject_id, subject_name, subject_description FROM teachers t LEFT JOIN " +
                "teachers_subjects ts ON  t.teacher_id = ts.teacher_id LEFT JOIN subjects s on ts.subject_id = s.subject_id ORDER BY " +
                "t.teacher_id, s.subject_id;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM teachers WHERE teacher_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT t.teacher_id, first_name, last_name, s.subject_id, subject_name, subject_description FROM teachers t LEFT JOIN " +
                "teachers_subjects ts ON  t.teacher_id = ts.teacher_id LEFT JOIN subjects s on ts.subject_id = s.subject_id " +
                "WHERE  t.teacher_id = ? ORDER BY t.teacher_id, s.subject_id;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO teachers (first_name, last_name) VALUES (?, ?);";
    }

    private String getQueryToGetBySubject() {
        return "SELECT t.teacher_id, first_name, last_name, s.subject_id, subject_name, subject_description " +
                "FROM teachers t LEFT JOIN teachers_subjects ts ON  t.teacher_id = ts.teacher_id LEFT JOIN " +
                "subjects s on ts.subject_id = s.subject_id WHERE  t.teacher_id IN " +
                "(SELECT t.teacher_id FROM teachers t LEFT JOIN teachers_subjects ts ON t.teacher_id = ts.teacher_id " +
                "LEFT JOIN subjects s on ts.subject_id = s.subject_id WHERE s.subject_id = ?) ORDER BY t.teacher_id, s.subject_id;";
    }

    private void addTeacherSubjects(Teacher teacher) {
        List<Subject> subjects = teacher.getSubjects();
        if (subjects != null) {
            for (Subject subject : subjects) {
                if (subject.getId() == null) {
                    subjectDao.addAll(subjects);
                }
                subjectDao.addTeacherToSubject(subject, teacher);
            }
        }
    }
}