package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.SubjectDao;
import com.kharchenko.university.dao.mappers.SubjectMapper;

import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class SubjectDaoImpl extends AbstractDao<Subject> implements SubjectDao {

    protected SubjectDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new SubjectMapper(), jdbcTemplate);
    }

    @Override
    public Subject add(Subject subject) {
        String query = "INSERT INTO subjects (subject_name, subject_description) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"subject_id"});
            fillRow(statement, subject);
            return statement;
        }, keyHolder);
        subject.setId(keyHolder.getKey().intValue());
        return subject;
    }

    @Override
    public void update(Subject subject) {
        String query = "UPDATE subjects SET subject_name = ?, subject_description = ? WHERE subject_id = ?;";
        jdbcTemplate.update(query, subject.getName(), subject.getDescription(), subject.getId());
    }

    @Override
    public void addTeacherToSubject(Subject subject, Teacher teacher) {
        String query = "INSERT INTO teachers_subjects (subject_id, teacher_id) VALUES (?, ?);";
        jdbcTemplate.update(query, subject.getId(), teacher.getId());
    }

    @Override
    public void addSubjectToGroup(Subject subject, Group group) {
        String query = "INSERT INTO groups_subjects (subject_id, group_id) VALUES (?, ?);";
        jdbcTemplate.update(query, subject.getId(), group.getId());
    }

    @Override
    public void removeSubjectFromGroup(Subject subject, Group group) {
        String query = "DELETE FROM groups_subjects WHERE group_id = ? AND subject_id = ?;";
        jdbcTemplate.update(query, subject.getId(), group.getId());
    }

    @Override
    public void removeSubjectFromTeacher(Subject subject, Teacher teacher) {
        String query = "DELETE FROM teachers_subjects WHERE subject_id = ? AND teacher_id = ?;";
        jdbcTemplate.update(query, subject.getId(), teacher.getId());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Subject subject) throws SQLException {
        statement.setString(1, subject.getName());
        statement.setString(2, subject.getDescription());
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT * FROM subjects;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM subjects WHERE subject_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT * FROM subjects WHERE subject_id = ?;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO subjects (subject_name, subject_description) VALUES (?, ?);";
    }
}
