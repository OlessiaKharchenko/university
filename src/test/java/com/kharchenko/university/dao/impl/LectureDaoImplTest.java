package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.ClassRoom;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LectureDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LectureDaoImpl lectureDao;
    @Autowired
    private ScheduleDaoImpl scheduleDao;
    @Autowired
    private SubjectDaoImpl subjectDao;
    @Autowired
    private TeacherDaoImpl teacherDao;
    @Autowired
    private GroupDaoImpl groupDao;

    @Test
    void add_shouldReturnNewLecture_whenAddNewLecture() {
        Subject subject = new Subject(null, "Name", "Description");
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "name"));
        Lecture lecture = new Lecture(null, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        Lecture actual = lectureDao.add(lecture);

        Subject subjectWithId = new Subject(6, "Name", "Description");
        Teacher teacherWithId = new Teacher(4, "Name", "Surname", null);
        ClassRoom classRoomWithId = new ClassRoom(4, 1, 130, new Faculty(3, "name"));
        Lecture expected = new Lecture(4, subjectWithId, teacherWithId, classRoomWithId, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertEquals(expected, actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewLecture() {
        Subject subject = new Subject(null, "Name", "description");
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(null, subject, teacher, classRoom, new ArrayList<>(),
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        lectureDao.add(lecture);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "lectures"));
    }

    @Test
    void getById_shouldReturnCorrectLectureByGivenId() {
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));
        Lecture expected = new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00));
        Lecture actual = lectureDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenLectureNotExist() {
        Optional<Lecture> expected = Optional.empty();
        Optional<Lecture> actual = lectureDao.getById(4);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllLectures() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom firstRoom = new ClassRoom(1, 1, 100, faculty);
        ClassRoom secondRoom = new ClassRoom(2, 2, 200, faculty);
        Subject firstSubject = new Subject(1, "Java", "Learn Java");
        Subject secondSubject = new Subject(2, "Sql", "Learn Sql");
        Teacher firstTeacher = new Teacher(1, "Bruce", "Eckel", null);
        Teacher secondTeacher = new Teacher(2, "Robert", "Martin", null);
        List<Group> firstGroups = new ArrayList<>();
        firstGroups.add(new Group(1, "AA-111", null, faculty));
        firstGroups.add(new Group(2, "BB-222", null, faculty));
        List<Group> secondGroups = new ArrayList<>();
        secondGroups.add(new Group(2, "BB-222", null, faculty));
        secondGroups.add(new Group(3, "CC-333", null, faculty));

        Lecture firstLecture = new Lecture(1, firstSubject, firstTeacher, firstRoom, firstGroups,
                LocalTime.of(9, 00), LocalTime.of(11, 00));
        Lecture secondLecture = new Lecture(2, secondSubject, secondTeacher, secondRoom, secondGroups,
                LocalTime.of(12, 00), LocalTime.of(14, 00));
        Lecture thirdLecture = new Lecture(3, firstSubject, firstTeacher, secondRoom, new ArrayList<>(),
                LocalTime.of(14, 00), LocalTime.of(16, 00));
        List<Lecture> expected = Arrays.asList(firstLecture, secondLecture, thirdLecture);
        List<Lecture> actual = lectureDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void update_shouldCorrectlyUpdateLectureRecord() {
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        lectureDao.update(lecture);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "lectures", "lecture_id=1 and start_time='07:00' and end_time='09:00'"));
    }

    @Test
    void addLectureToSchedule_shouldInsertNewRecordToSchedulesLectures() {
        Lecture lecture = lectureDao.getById(1).get();
        Schedule schedule = scheduleDao.getById(2).get();
        lectureDao.addLectureToSchedule(lecture, schedule);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules_lectures"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "schedules_lectures", "lecture_id=1 and schedule_id=2"));
    }

    @Test
    void removeLectureFromSchedule_shouldRemoveRecordFromSchedulesLectures() {
        Lecture lecture = lectureDao.getById(1).get();
        Schedule schedule = scheduleDao.getById(1).get();
        lectureDao.removeLectureFromSchedule(lecture, schedule);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules_lectures"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "schedules_lectures", "lecture_id=1 and schedule_id=1"));
    }

    @Test
    void getByClassRoom_shouldReturnAllLecturesByGivenClassRoom() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00));
        List<Lecture> expected = Arrays.asList(lecture);
        List<Lecture> actual = lectureDao.getByClassRoom(classRoom);
        assertEquals(expected, actual);
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheLecture() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            lectureDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteLectureRecord_whenReferencesToTheLectureNotExists() {
        lectureDao.deleteById(3);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "lectures"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "lectures",
                "lecture_id=3"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListLectures() {
        Faculty faculty = new Faculty(null, "Name");
        Subject subject = new Subject(null, "Name", "description");
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        ClassRoom classRoom = new ClassRoom(null, 1, 130, faculty);
        List<Lecture> lectures = new ArrayList<>();
        lectures.add(new Lecture(null, subject, teacher, classRoom, new ArrayList<>(),
                LocalTime.of(15, 00), LocalTime.of(17, 00)));
        lectureDao.addAll(lectures);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "lectures"));
    }

    @Test
    void getBySubject_shouldReturnAllLecturesByGivenSubject() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(2, 2, 200, faculty);
        Subject subject = new Subject(2, "Sql", "Learn Sql");
        Teacher teacher = new Teacher(2, "Robert", "Martin", null);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(2, "BB-222", null, faculty));
        groups.add(new Group(3, "CC-333", null, faculty));
        Lecture secondLecture = new Lecture(2, subject, teacher, classRoom, groups,
                LocalTime.of(12, 00), LocalTime.of(14, 00));

        List<Lecture> expected = Arrays.asList(secondLecture);
        Subject givenSubject = subjectDao.getById(2).get();
        List<Lecture> actual = lectureDao.getBySubject(givenSubject);
        assertEquals(expected, actual);
    }

    @Test
    void getTeacherLectures_shouldReturnAllLecturesByGivenTeacher() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(2, 2, 200, faculty);
        Subject subject = new Subject(2, "Sql", "Learn Sql");
        Teacher teacher = new Teacher(2, "Robert", "Martin", null);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(2, "BB-222", null, faculty));
        groups.add(new Group(3, "CC-333", null, faculty));
        Lecture lecture = new Lecture(2, subject, teacher, classRoom, groups,
                LocalTime.of(12, 00), LocalTime.of(14, 00));

        List<Lecture> expected = Arrays.asList(lecture);
        Teacher givenTeacher = teacherDao.getById(2).get();
        List<Lecture> actual = lectureDao.getTeacherLectures(givenTeacher);
        assertEquals(expected, actual);
    }

    @Test
    void getGroupLectures_shouldReturnAllLecturesByGivenGroup() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00));

        List<Lecture> expected = Arrays.asList(lecture);
        Group group = groupDao.getById(1).get();
        List<Lecture> actual = lectureDao.getGroupLectures(group);
        assertEquals(expected, actual);
    }
}
