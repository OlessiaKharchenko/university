package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.mappers.LectureMapper;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.ClassRoom;
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
public class LectureDaoImpl extends AbstractDao<Lecture> implements LectureDao {

    @Autowired
    private SubjectDaoImpl subjectDao;
    @Autowired
    private TeacherDaoImpl teacherDao;
    @Autowired
    private ClassRoomDaoImpl classRoomDao;
    @Autowired
    private GroupDaoImpl groupDao;

    protected LectureDaoImpl(JdbcTemplate jdbcTemplate) {
        super(new LectureMapper(), jdbcTemplate);
    }

    @Override
    public Lecture add(Lecture lecture) {
        String query = "INSERT INTO lectures (start_time, end_time, subject_id, teacher_id, classroom_id) VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"lecture_id"});
            fillRow(statement, lecture);
            return statement;
        }, keyHolder);
        lecture.setId(keyHolder.getKey().intValue());
        return lecture;
    }

    @Override
    public void update(Lecture lecture) {
        String query = "UPDATE lectures SET subject_id = ?, teacher_id = ?, classroom_id = ?, start_time = ?," +
                "end_time = ? WHERE lecture_id = ?;";
        jdbcTemplate.update(query, lecture.getSubject().getId(), lecture.getTeacher().getId(), lecture.getClassRoom().getId(),
                lecture.getStartTime(), lecture.getEndTime(), lecture.getId());
    }

    @Override
    public void addLectureToSchedule(Lecture lecture, Schedule schedule) {
        String query = "INSERT INTO schedules_lectures (schedule_id, lecture_id) VALUES (?, ?);";
        jdbcTemplate.update(query, schedule.getId(), lecture.getId());
    }

    @Override
    public List<Lecture> getByClassRoom(ClassRoom classRoom) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetByClassRoom(), classRoom.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lecture> getBySubject(Subject subject) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetBySubject(), subject.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lecture> getTeacherLectures(Teacher teacher) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetTeacherLectures(), teacher.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lecture> getGroupLectures(Group group) {
        return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetGroupLectures(), group.getId()), mapper)
                .collect(Collectors.toList());
    }

    @Override
    protected void fillRow(PreparedStatement statement, Lecture lecture) throws SQLException {
        statement.setObject(1, lecture.getStartTime());
        statement.setObject(2, lecture.getEndTime());
        Subject subject = lecture.getSubject();
        if (subject.getId() == null) {
            subjectDao.add(subject);
        }
        statement.setInt(3, subject.getId());
        Teacher teacher = lecture.getTeacher();
        if (teacher.getId() == null) {
            teacherDao.add(teacher);
        }
        statement.setInt(4, teacher.getId());
        ClassRoom classRoom = lecture.getClassRoom();
        if (classRoom.getId() == null) {
            classRoomDao.add(classRoom);
        }
        statement.setInt(5, classRoom.getId());
        addLectureGroups(lecture);
    }

    @Override
    protected String getQueryToGetAll() {
        return "SELECT l.lecture_id, start_time, end_time, c.classroom_id, building_number, room_number, f.faculty_id, " +
                "faculty_name, s.subject_id, subject_name, subject_description, t.teacher_id, first_name, last_name, " +
                "g.group_id, group_name FROM lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id LEFT JOIN classrooms c ON l.classroom_id = c.classroom_id " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id LEFT JOIN subjects s ON l.subject_id = s.subject_id " +
                "LEFT JOIN teachers t ON l.teacher_id = t.teacher_id ORDER BY l.lecture_id, g.group_id;";
    }

    @Override
    protected String getQueryToDeleteById() {
        return "DELETE FROM lectures WHERE lecture_id = ?;";
    }

    @Override
    protected String getQueryToGetById() {
        return "SELECT l.lecture_id, start_time, end_time, c.classroom_id, building_number, room_number, f.faculty_id, " +
                "faculty_name, s.subject_id, subject_name, subject_description, t.teacher_id, first_name, last_name, " +
                "g.group_id, group_name FROM lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id LEFT JOIN classrooms c ON l.classroom_id = c.classroom_id " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id LEFT JOIN subjects s ON l.subject_id = s.subject_id " +
                "LEFT JOIN teachers t ON l.teacher_id = t.teacher_id WHERE l.lecture_id = ? ORDER BY l.lecture_id, g.group_id;";
    }

    @Override
    protected String getQueryToAddAll() {
        return "INSERT INTO lectures (start_time, end_time, subject_id, teacher_id, classroom_id) VALUES (?, ?, ?, ?, ?);";
    }

    private String getQueryToGetByClassRoom() {
        return "SELECT l.lecture_id, start_time, end_time, c.classroom_id, building_number, room_number, f.faculty_id, " +
                "faculty_name, s.subject_id, subject_name, subject_description, t.teacher_id, first_name, last_name, " +
                "g.group_id, group_name FROM lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id LEFT JOIN classrooms c ON l.classroom_id = c.classroom_id " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id LEFT JOIN subjects s ON l.subject_id = s.subject_id " +
                "LEFT JOIN teachers t ON l.teacher_id = t.teacher_id WHERE c.classroom_id = ? ORDER BY l.lecture_id;";
    }

    private String getQueryToGetBySubject() {
        return "SELECT l.lecture_id, start_time, end_time, c.classroom_id, building_number, room_number, f.faculty_id, " +
                "faculty_name, s.subject_id, subject_name, subject_description, t.teacher_id, first_name, last_name, " +
                "g.group_id, group_name FROM lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id LEFT JOIN classrooms c ON l.classroom_id = c.classroom_id " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id LEFT JOIN subjects s ON l.subject_id = s.subject_id " +
                "LEFT JOIN teachers t ON l.teacher_id = t.teacher_id WHERE s.subject_id = ? ORDER BY l.lecture_id;";
    }

    private String getQueryToGetTeacherLectures() {
        return "SELECT l.lecture_id, start_time, end_time, c.classroom_id, building_number, room_number, f.faculty_id," +
                "faculty_name, s.subject_id, subject_name, subject_description, t.teacher_id, first_name, last_name, " +
                "g.group_id, group_name FROM lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id LEFT JOIN classrooms c ON l.classroom_id = c.classroom_id " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id LEFT JOIN subjects s ON l.subject_id = s.subject_id " +
                "LEFT JOIN teachers t ON l.teacher_id = t.teacher_id WHERE t.teacher_id = ? ORDER BY l.lecture_id;";
    }

    private String getQueryToGetGroupLectures() {
        return "SELECT l.lecture_id, start_time, end_time, c.classroom_id, building_number, room_number, f.faculty_id, " +
                "faculty_name, s.subject_id, subject_name, subject_description, t.teacher_id, first_name, last_name, " +
                "g.group_id, group_name FROM lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id LEFT JOIN classrooms c ON l.classroom_id = c.classroom_id " +
                "LEFT JOIN faculties f ON c.faculty_id = f.faculty_id LEFT JOIN subjects s ON l.subject_id = s.subject_id " +
                "LEFT JOIN teachers t ON l.teacher_id = t.teacher_id WHERE l.lecture_id IN (SELECT l.lecture_id FROM " +
                "lectures l LEFT JOIN groups_lectures gl ON l.lecture_id = gl.lecture_id " +
                "LEFT JOIN groups g ON gl.group_id = g.group_id WHERE g.group_id = ?) ORDER BY l.lecture_id;";
    }

    private void addLectureGroups(Lecture lecture) {
        List<Group> groups = lecture.getGroups();
        if (groups != null) {
            for (Group group : groups) {
                if (group.getId() == null) {
                    groupDao.addAll(groups);
                }
                groupDao.addLectureToGroup(lecture, group);
            }
        }
    }
}