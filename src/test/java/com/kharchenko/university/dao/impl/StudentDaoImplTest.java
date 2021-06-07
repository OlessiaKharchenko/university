package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StudentDaoImpl studentDao;

    @Test
    void add_shouldReturnNewStudent_whenAddNewStudent() {
        Faculty faculty = new Faculty(1, "Programming");
        Student student = new Student(null, "Nikita", "Nikitin",
                new Group(null, "FF-15", null, faculty));
        Student actual = studentDao.add(student);
        assertEquals(new Student(7, "Nikita", "Nikitin",
                new Group(5, "FF-15", null, faculty)), actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewStudent() {
        Faculty faculty = new Faculty(1, "Programming");
        Student student = new Student(null, "Nikita", "Nikitin",
                new Group(null, "FF-15", null, faculty));
        studentDao.add(student);
        assertEquals(7, JdbcTestUtils.countRowsInTable(jdbcTemplate, "students"));
    }

    @Test
    void getById_shouldReturnCorrectStudentByGivenId() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "AA-111", subjects, faculty);
        Student expected = new Student(1, "Ivan", "Ivanov", group);
        Student actual = studentDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenStudentNotExist() {
        Optional<Student> expected = Optional.empty();
        Optional<Student> actual = studentDao.getById(7);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllStudents() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> first = new ArrayList<>();
        first.add(new Subject(1, "Java", "Learn Java"));
        first.add(new Subject(2, "Sql", "Learn Sql"));
        List<Subject> second = new ArrayList<>();
        second.add(new Subject(3, "Spring", "Learn Spring"));
        second.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        Group one = new Group(1, "AA-111", first, faculty);
        Group two = new Group(2, "BB-222", second, faculty);
        List<Student> expected = new ArrayList<>();
        expected.add(new Student(1, "Ivan", "Ivanov", one));
        expected.add(new Student(2, "Petr", "Petrov", one));
        expected.add(new Student(3, "Anton", "Antonov", two));
        expected.add(new Student(4, "Olha", "Olhova", two));
        expected.add(new Student(5, "Mariia", "Mariyk", one));
        expected.add(new Student(6, "Vasyl", "Valyliev", two));
        assertEquals(expected, studentDao.getAll());
    }

    @Test
    void getAll_shouldReturnCorrectStudentRecordsCount() {
        int studentsSize = studentDao.getAll().size();
        assertEquals(studentsSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "students"));
    }

    @Test
    void update_shouldCorrectlyUpdateStudentRecord() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "AA-111", subjects, faculty);
        Student student = new Student(1, "Nikita", "Nikitin", group);
        studentDao.update(student);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "students", "student_id=1 and first_name='Nikita' and " +
                        "last_name='Nikitin'"));
    }

    @Test
    void deleteById_shouldDeleteStudentRecordByGivenId() {
        studentDao.deleteById(1);
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "students"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "students",
                "student_id=1"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListStudents() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(null, "FF-15", null, faculty);
        List<Student> students = new ArrayList<>();
        students.add(new Student(null, "Vadym", "Ivanov", group));
        students.add(new Student(null, "Yuriy", "Degtiaruk", group));
        studentDao.addAll(students);
        assertEquals(8, JdbcTestUtils.countRowsInTable(jdbcTemplate, "students"));
    }

    @Test
    void getGroupStudents_shouldReturnAllStudentsByGivenGroup() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> first = new ArrayList<>();
        first.add(new Subject(1, "Java", "Learn Java"));
        first.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "AA-111", first, faculty);
        List<Student> expected = new ArrayList<>();
        expected.add(new Student(1, "Ivan", "Ivanov", group));
        expected.add(new Student(2, "Petr", "Petrov", group));
        expected.add(new Student(5, "Mariia", "Mariyk", group));
        List<Student> actual = studentDao.getGroupStudents(group);
        assertEquals(expected, actual);
    }
}