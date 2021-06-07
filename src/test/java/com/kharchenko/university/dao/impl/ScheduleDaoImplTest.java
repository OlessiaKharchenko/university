package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.Faculty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ScheduleDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ScheduleDaoImpl scheduleDao;
    @Autowired
    private LectureDaoImpl lectureDao;

    @Test
    void add_shouldReturnNewSchedule_whenAddNewSchedule() {
        Schedule schedule = new Schedule(null, null, LocalDate.of(2021, 05, 27),
                new Faculty(null, "Name"));
        Schedule expected = new Schedule(3, null, LocalDate.of(2021, 05, 27),
                new Faculty(3, "Name"));
        Schedule actual = scheduleDao.add(schedule);
        assertEquals(expected, actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewSchedule() {
        Schedule schedule = new Schedule(null, null, LocalDate.of(2021, 05, 27),
                new Faculty(null, "Name"));
        scheduleDao.add(schedule);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules"));
    }

    @Test
    void getById_shouldReturnCorrectScheduleByGivenId() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Lecture> lectures = new ArrayList<>();
        lectures.add(new Lecture(0, null, null, null, null,
                null, null));
        Schedule expected = new Schedule(2, lectures, LocalDate.of(2021, 05, 25), faculty);
        Schedule actual = scheduleDao.getById(2).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenScheduleNotExist() {
        Optional<Schedule> expected = Optional.empty();
        Optional<Schedule> actual = scheduleDao.getById(4);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllSchedules() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Lecture> firstLectures = new ArrayList<>();
        firstLectures.add(new Lecture(1, null, null, null, null,
                null, null));
        firstLectures.add(new Lecture(2, null, null, null, null,
                null, null));
        List<Lecture> secondLectures = new ArrayList<>();
        secondLectures.add(new Lecture(0, null, null, null, null,
                null, null));

        List<Schedule> expected = new ArrayList<>();
        expected.add(new Schedule(1, firstLectures, LocalDate.of(2021, 05, 24), faculty));
        expected.add(new Schedule(2, secondLectures, LocalDate.of(2021, 05, 25), faculty));
        List<Schedule> actual = scheduleDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnCorrectScheduleRecordsCount() {
        int groupsSize = scheduleDao.getAll().size();
        assertEquals(groupsSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules"));
    }

    @Test
    void update_shouldCorrectlyUpdateScheduleRecord() {
        Faculty faculty = new Faculty(2, "Management");
        Schedule schedule = new Schedule(2, null, LocalDate.of(2021, 05, 26), faculty);
        scheduleDao.update(schedule);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "schedules", "schedule_id=2 and date='2021-05-26' and faculty_id=2"));
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheSchedule() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            scheduleDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteScheduleRecord_whenReferencesToTheScheduleNotExists() {
        scheduleDao.deleteById(2);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "schedules",
                "schedule_id=2"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(new Schedule(null, null, LocalDate.of(2021, 05, 28),
                new Faculty(null, "Name")));
        scheduleDao.addAll(schedules);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules"));
    }

    @Test
    void getByLecture_shouldReturnAllSchedulesByGivenLecture() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Lecture> firstLectures = new ArrayList<>();
        firstLectures.add(new Lecture(1, null, null, null, null,
                null, null));
        firstLectures.add(new Lecture(2, null, null, null, null,
                null, null));
        List<Schedule> expected = new ArrayList<>();
        expected.add(new Schedule(1, firstLectures, LocalDate.of(2021, 05, 24), faculty));
        Lecture lecture = lectureDao.getById(1).get();
        List<Schedule> actual = scheduleDao.getByLecture(lecture);
        assertEquals(expected, actual);
    }

    @Test
    void getByFaculty_shouldReturnAllSchedulesByGivenFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Lecture> firstLectures = new ArrayList<>();
        firstLectures.add(new Lecture(1, null, null, null, null,
                null, null));
        firstLectures.add(new Lecture(2, null, null, null, null,
                null, null));
        List<Lecture> secondLectures = new ArrayList<>();
        secondLectures.add(new Lecture(0, null, null, null, null,
                null, null));

        List<Schedule> expected = new ArrayList<>();
        expected.add(new Schedule(1, firstLectures, LocalDate.of(2021, 05, 24), faculty));
        expected.add(new Schedule(2, secondLectures, LocalDate.of(2021, 05, 25), faculty));
        List<Schedule> actual = scheduleDao.getByFaculty(faculty);
        assertEquals(expected, actual);
    }
}