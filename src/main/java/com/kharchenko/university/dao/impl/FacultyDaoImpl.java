package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.FacultyDao;
import com.kharchenko.university.dao.mappers.FacultyMapper;
import com.kharchenko.university.model.Faculty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class FacultyDaoImpl extends AbstractDao<Faculty> implements FacultyDao {

    public FacultyDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new FacultyMapper(), jdbcTemplate);
    }

    @Override
    public Faculty add(Faculty faculty) {
        String query = "INSERT INTO faculties (faculty_name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"faculty_id"});
            fillRow(statement, faculty);
            return statement;
        }, keyHolder);
        faculty.setId(keyHolder.getKey().intValue());
        return faculty;
    }

    @Override
    public void update(Faculty faculty) {
        String query = "UPDATE faculties SET faculty_name = ? WHERE faculty_id = ?;";
        jdbcTemplate.update(query, faculty.getName(), faculty.getId());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Faculty faculty) throws SQLException {
        statement.setString(1, faculty.getName());
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT * FROM faculties;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM faculties WHERE faculty_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT * FROM faculties WHERE faculty_id = ?;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO faculties (faculty_name) VALUES (?);";
    }
}