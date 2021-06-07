package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
import com.kharchenko.university.model.Group;
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
class SubjectDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SubjectDaoImpl subjectDao;
    @Autowired
    private TeacherDaoImpl teacherDao;
    @Autowired
    private GroupDaoImpl groupDao;

    @Test
    void add_shouldReturnNewSubject_whenAddNewSubject() {
        Subject subject = new Subject(null, "Jdbc", "Learn Jdbc");
        Subject actual = subjectDao.add(subject);
        assertEquals(new Subject(6, "Jdbc", "Learn Jdbc"), actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewSubject() {
        Subject subject = new Subject(null, "Jdbc", "Learn Jdbc");
        subjectDao.add(subject);
        assertEquals(6, JdbcTestUtils.countRowsInTable(jdbcTemplate, "subjects"));
    }

    @Test
    void getAll_shouldReturnAllSubjects() {
        List<Subject> expected = new ArrayList<>();
        expected.add(new Subject(1, "Java", "Learn Java"));
        expected.add(new Subject(2, "Sql", "Learn Sql"));
        expected.add(new Subject(3, "Spring", "Learn Spring"));
        expected.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        expected.add(new Subject(5, "Junit", "Learn Junit"));
        List<Subject> actual = subjectDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnCorrectSubjectRecordsCount() {
        int groupsSize = subjectDao.getAll().size();
        assertEquals(groupsSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "subjects"));
    }

    @Test
    void getById_shouldReturnCorrectSubjectByGivenId() {
        Subject expected = new Subject(1, "Java", "Learn Java");
        Subject actual = subjectDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenSubjectNotExist() {
        Optional<Subject> expected = Optional.empty();
        Optional<Subject> actual = subjectDao.getById(6);
        assertEquals(expected, actual);
    }

    @Test
    void update_shouldCorrectlyUpdateSubjectRecord() {
        Subject subject = new Subject(1, "English", "Learn English");
        subjectDao.update(subject);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "subjects", "subject_id=1 and subject_name='English' and " +
                        "subject_description='Learn English'"));
    }

    @Test
    void addTeacherToSubject_shouldInsertNewRecordToTeachersSubjects() {
        Subject subject = subjectDao.getById(2).get();
        Teacher teacher = teacherDao.getById(1).get();
        subjectDao.addTeacherToSubject(subject, teacher);
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "teachers_subjects"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "teachers_subjects", "subject_id=2 and teacher_id=1"));
    }

    @Test
    void addSubjectToGroup_shouldInsertNewRecordToGroupsSubjects() {
        Subject subject = subjectDao.getById(3).get();
        Group group = groupDao.getById(1).get();
        subjectDao.addSubjectToGroup(subject, group);
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "groups_subjects"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "groups_subjects", "subject_id=3 and group_id=1"));
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheSubject() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            subjectDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteSubjectRecord_whenReferencesToTheSubjectNotExists() {
        subjectDao.deleteById(5);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "subjects"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "subjects",
                "subject_id=5"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListSubjects() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(null, "Sixth subject", "Description"));
        subjects.add(new Subject(null, "Seventh subject", "Description"));
        subjectDao.addAll(subjects);
        assertEquals(7, JdbcTestUtils.countRowsInTable(jdbcTemplate, "subjects"));
    }

    @Test
    void removeSubjectFromGroup_shouldRemoveRecordFromGroupsSubjects() {
        Subject subject = subjectDao.getById(1).get();
        Group group = groupDao.getById(1).get();
        subjectDao.removeSubjectFromGroup(subject, group);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "groups_subjects"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "groups_subjects", "subject_id=1 and group_id=1"));

    }

    @Test
    void removeSubjectFromTeacher_shouldRemoveRecordFromTeachersSubjects() {
        Subject subject = subjectDao.getById(1).get();
        Teacher teacher = teacherDao.getById(1).get();
        subjectDao.removeSubjectFromTeacher(subject, teacher);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "teachers_subjects"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "teachers_subjects", "subject_id=1 and teacher_id=1"));
    }
}
