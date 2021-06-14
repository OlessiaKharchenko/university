package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.dao.mappers.ScheduleMapper;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;
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
public class ScheduleDaoImpl extends AbstractDao<Schedule> implements ScheduleDao {

    @Autowired
    private LectureDaoImpl lectureDao;
    @Autowired
    private FacultyDaoImpl facultyDao;

    public ScheduleDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new ScheduleMapper(), jdbcTemplate);
    }

    @Override
    public Schedule add(Schedule schedule) {
        String query = "INSERT INTO schedules (date, faculty_id) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"schedule_id"});
            fillRow(statement, schedule);
            return statement;
        }, keyHolder);
        schedule.setId(keyHolder.getKey().intValue());
        addScheduleLectures(schedule);
        return schedule;
    }

    @Override
    public void update(Schedule schedule) {
        String query = "UPDATE schedules SET date = ?, faculty_id = ? WHERE schedule_id = ?;";
        jdbcTemplate.update(query, schedule.getDate(), schedule.getFaculty().getId(), schedule.getId());
    }

    @Override
    public List<Schedule> getByLecture(Lecture lecture) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetByLecture(), lecture.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> getByFaculty(Faculty faculty) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetByFaculty(), faculty.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Schedule schedule) throws SQLException {
        statement.setObject(1, schedule.getDate());
        Faculty faculty = schedule.getFaculty();
        if (faculty.getId() == null) {
            facultyDao.add(faculty);
        }
        statement.setInt(2, faculty.getId());
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT s.schedule_id, date, f.faculty_id, faculty_name, l.lecture_id FROM schedules s " +
                "LEFT JOIN schedules_lectures shl ON s.schedule_id = shl.schedule_id " +
                "LEFT JOIN lectures l ON shl.lecture_id = l.lecture_id " +
                "LEFT JOIN faculties f ON s.faculty_id = f.faculty_id;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM schedules WHERE schedule_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT s.schedule_id, date, f.faculty_id, faculty_name, l.lecture_id FROM schedules s " +
                "LEFT JOIN schedules_lectures shl ON s.schedule_id = shl.schedule_id " +
                "LEFT JOIN lectures l ON shl.lecture_id = l.lecture_id " +
                "LEFT JOIN faculties f ON s.faculty_id = f.faculty_id WHERE s.schedule_id = ?;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO schedules (date, faculty_id) VALUES (?, ?);";
    }

    private String getQueryToGetByLecture() {
        return "SELECT s.schedule_id, date, f.faculty_id, faculty_name, l.lecture_id FROM schedules s " +
                "LEFT JOIN schedules_lectures shl ON s.schedule_id = shl.schedule_id " +
                "LEFT JOIN lectures l ON shl.lecture_id = l.lecture_id LEFT JOIN faculties f ON s.faculty_id = f.faculty_id " +
                "WHERE s.schedule_id IN (SELECT s.schedule_id FROM schedules s LEFT JOIN schedules_lectures shl " +
                "ON s.schedule_id = shl.schedule_id LEFT JOIN lectures l ON shl.lecture_id = l.lecture_id WHERE l.lecture_id = ?);";
    }

    private String getQueryToGetByFaculty() {
        return "SELECT s.schedule_id, date, f.faculty_id, faculty_name, l.lecture_id FROM schedules s " +
                "LEFT JOIN schedules_lectures shl ON s.schedule_id = shl.schedule_id " +
                "LEFT JOIN lectures l ON shl.lecture_id = l.lecture_id " +
                "LEFT JOIN faculties f ON s.faculty_id = f.faculty_id WHERE f.faculty_id = ?;";
    }

    private void addScheduleLectures(Schedule schedule) {
        List<Lecture> lectures = schedule.getLectures();
        if (lectures != null) {
            for (Lecture lecture : lectures) {
                if (lecture.getId() == null) {
                    lectureDao.add(lecture);
                }
                lectureDao.addLectureToSchedule(lecture, schedule);
            }
        }
    }
}
