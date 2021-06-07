package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.mappers.GroupMapper;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
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
public class GroupDaoImpl extends AbstractDao<Group> implements GroupDao {

    @Autowired
    private SubjectDaoImpl subjectDao;
    @Autowired
    private FacultyDaoImpl facultyDao;

    protected GroupDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new GroupMapper(), jdbcTemplate);
    }

    @Override
    public Group add(Group group) {
        String query = "INSERT INTO groups (group_name, faculty_id) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"group_id"});
            fillRow(statement, group);
            return statement;
        }, keyHolder);
        group.setId(keyHolder.getKey().intValue());
        addGroupSubjects(group);
        return group;
    }

    @Override
    public void update(Group group) {
        String query = "UPDATE groups SET group_name = ?, faculty_id = ? WHERE group_id = ?;";
        jdbcTemplate.update(query, group.getName(), group.getFaculty().getId(), group.getId());
    }

    @Override
    public void addLectureToGroup(Lecture lecture, Group group) {
        String query = "INSERT INTO groups_lectures (lecture_id, group_id) VALUES (?, ?);";
        jdbcTemplate.update(query, lecture.getId(), group.getId());
    }

    @Override
    public void removeLectureFromGroup(Lecture lecture, Group group) {
        String query = "DELETE FROM groups_lectures WHERE group_id = ? AND lecture_id = ?;";
        jdbcTemplate.update(query, lecture.getId(), group.getId());
    }

    @Override
    public List<Group> getBySubject(Subject subject) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetBySubject(), subject.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getByFaculty(Faculty faculty) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetByFaculty(), faculty.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Group group) throws SQLException {
        statement.setString(1, group.getName());
        Faculty faculty = group.getFaculty();
        if (faculty.getId() == null) {
            facultyDao.add(faculty);
        }
        statement.setInt(2, faculty.getId());
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT g.group_id, group_name, f.faculty_id, faculty_name, s.subject_id, subject_name, subject_description " +
                "FROM groups g LEFT JOIN groups_subjects gs ON  g.group_id = gs.group_id LEFT JOIN subjects s " +
                "ON gs.subject_id = s.subject_id LEFT JOIN  faculties f ON g.faculty_id = f.faculty_id ORDER BY g.group_id, s.subject_id;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM groups WHERE group_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT g.group_id, group_name, f.faculty_id, faculty_name, s.subject_id, subject_name, subject_description " +
                "FROM groups g LEFT JOIN groups_subjects gs ON  g.group_id = gs.group_id LEFT JOIN subjects s ON " +
                "gs.subject_id = s.subject_id LEFT JOIN  faculties f ON g.faculty_id = f.faculty_id " +
                "WHERE g.group_id = ? ORDER BY g.group_id, s.subject_id;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO groups (group_name, faculty_id) VALUES (?, ?);";
    }

    private String getQueryToGetBySubject() {
        return "SELECT g.group_id, group_name, f.faculty_id, faculty_name, s.subject_id, subject_name, subject_description " +
                "FROM groups g LEFT JOIN groups_subjects gs ON  g.group_id = gs.group_id LEFT JOIN subjects s ON " +
                "gs.subject_id = s.subject_id LEFT JOIN  faculties f ON g.faculty_id = f.faculty_id " +
                "WHERE g.group_id IN (SELECT g.group_id FROM groups g LEFT JOIN groups_subjects gs ON g.group_id = gs.group_id " +
                "LEFT JOIN subjects s ON gs.subject_id = s.subject_id WHERE s.subject_id = ?) ORDER BY g.group_id, s.subject_id;";
    }

    private String getQueryToGetByFaculty() {
        return "SELECT g.group_id, group_name, f.faculty_id, faculty_name, s.subject_id, subject_name, subject_description " +
                "FROM groups g LEFT JOIN groups_subjects gs ON  g.group_id = gs.group_id LEFT JOIN subjects s ON " +
                "gs.subject_id = s.subject_id LEFT JOIN  faculties f ON g.faculty_id = f.faculty_id " +
                "WHERE f.faculty_id = ? ORDER BY g.group_id, s.subject_id;";
    }

    private void addGroupSubjects(Group group) {
        List<Subject> subjects = group.getSubjects();
        if (subjects != null) {
            for (Subject subject : subjects) {
                if (subject.getId() == null) {
                    subjectDao.add(subject);
                }
                subjectDao.addSubjectToGroup(subject, group);
            }
        }
    }
}
