package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TeacherDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TeacherDaoImpl teacherDao;
    @Autowired
    private SubjectDaoImpl subjectDao;

    @Test
    void add_shouldReturnNewTeacher_whenAddNewTeacher() {
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        Teacher actual = teacherDao.add(teacher);
        assertEquals(new Teacher(4, "Name", "Surname", null), actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewTeacher() {
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        teacherDao.add(teacher);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "teachers"));
    }

    @Test
    void getById_shouldReturnCorrectTeacherByGivenId() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        Teacher expected = new Teacher(1, "Bruce", "Eckel", subjects);
        Teacher actual = teacherDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenTeacherNotExist() {
        Optional<Teacher> expected = Optional.empty();
        Optional<Teacher> actual = teacherDao.getById(4);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllTeachers() {
        List<Subject> firstSubjects = new ArrayList<>();
        firstSubjects.add(new Subject(1, "Java", "Learn Java"));
        firstSubjects.add(new Subject(3, "Spring", "Learn Spring"));
        List<Subject> secondSubjects = new ArrayList<>();
        secondSubjects.add(new Subject(2, "Sql", "Learn Sql"));
        secondSubjects.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        List<Teacher> expected = new ArrayList<>();
        expected.add(new Teacher(1, "Bruce", "Eckel", firstSubjects));
        expected.add(new Teacher(2, "Robert", "Martin", secondSubjects));
        expected.add(new Teacher(3, "James", "Gosling", new ArrayList<>()));
        List<Teacher> actual = teacherDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnCorrectTeacherRecordsCount() {
        int teachersSize = teacherDao.getAll().size();
        assertEquals(teachersSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "teachers"));
    }

    @Test
    void update_shouldCorrectlyUpdateTeacherRecord() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        Teacher teacher = new Teacher(1, "Name", "Surname", subjects);
        teacherDao.update(teacher);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "teachers", "teacher_id=1 and first_name='Name' and " +
                        "last_name='Surname'"));
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheTeacher() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            teacherDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteTeacherRecord_whenReferencesToTheTeacherNotExists() {
        teacherDao.deleteById(3);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "teachers"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "teachers",
                "teacher_id=3"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(null, "Name", "Surname", null));
        teachers.add(new Teacher(null, "Name", "Surname", null));
        teacherDao.addAll(teachers);
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "teachers"));
    }

    @Test
    void getBySubject_shouldReturnAllTeachersByGivenSubject() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        List<Teacher> expected = new ArrayList<>();
        expected.add(new Teacher(1, "Bruce", "Eckel", subjects));
        Subject subject = subjectDao.getById(1).get();
        List<Teacher> actual = teacherDao.getBySubject(subject);
        assertEquals(expected, actual);
    }
}