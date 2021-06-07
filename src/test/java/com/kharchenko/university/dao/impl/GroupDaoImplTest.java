package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
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

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GroupDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GroupDaoImpl groupDao;
    @Autowired
    private LectureDaoImpl lectureDao;
    @Autowired
    private SubjectDaoImpl subjectDao;

    @Test
    void add_shouldReturnNewGroup_whenAddNewGroup() {
        Group group = new Group(null, "New group", null, new Faculty(null, "Name"));
        Group actual = groupDao.add(group);
        assertEquals(new Group(5, "New group", null, new Faculty(3, "Name")), actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewGroup() {
        Group group = new Group(null, "New group", null, new Faculty(null, "Name"));
        groupDao.add(group);
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "groups"));
    }

    @Test
    void getById_shouldReturnCorrectGroupByGivenId() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group expected = new Group(1, "AA-111", subjects, faculty);
        Group actual = groupDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenGroupNotExist() {
        Optional<Group> expected = Optional.empty();
        Optional<Group> actual = groupDao.getById(5);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllGroups() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> firstGroupSubjects = new ArrayList<>();
        firstGroupSubjects.add(new Subject(1, "Java", "Learn Java"));
        firstGroupSubjects.add(new Subject(2, "Sql", "Learn Sql"));
        List<Subject> secondGroupSubjects = new ArrayList<>();
        secondGroupSubjects.add(new Subject(3, "Spring", "Learn Spring"));
        secondGroupSubjects.add(new Subject(4, "Hibernate", "Learn Hibernate"));

        List<Group> expected = new ArrayList<>();
        expected.add(new Group(1, "AA-111", firstGroupSubjects, faculty));
        expected.add(new Group(2, "BB-222", secondGroupSubjects, faculty));
        expected.add(new Group(3, "CC-333", new ArrayList<>(), faculty));
        expected.add(new Group(4, "DD-444", new ArrayList<>(), faculty));
        List<Group> actual = groupDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnCorrectGroupRecordsCount() {
        int groupsSize = groupDao.getAll().size();
        assertEquals(groupsSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "groups"));
    }

    @Test
    void update_shouldCorrectlyUpdateGroupRecord() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "New Group", subjects, faculty);
        groupDao.update(group);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "groups", "group_id=1 and group_name='New Group' and faculty_id=1"));
    }

    @Test
    void addLectureToGroup_shouldInsertNewRecordToGroupsLectures() {
        Lecture lecture = lectureDao.getById(1).get();
        Group group = groupDao.getById(3).get();
        groupDao.addLectureToGroup(lecture, group);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "groups_lectures", "group_id=3 and lecture_id=1"));
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheGroup() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            groupDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteGroupRecord_whenReferencesToTheGroupNotExists() {
        groupDao.deleteById(4);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "groups"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "groups",
                "group_id=4"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(null, "Fifth group", null, new Faculty(null, "Name")));
        groupDao.addAll(groups);
        assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "groups"));
    }

    @Test
    void getBySubject_shouldReturnAllGroupsByGivenSubject() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        List<Group> expected = Arrays.asList(new Group(1, "AA-111", subjects, faculty));
        Subject subject = subjectDao.getById(1).get();
        List<Group> actual = groupDao.getBySubject(subject);
        assertEquals(expected, actual);
    }

    @Test
    void getByFaculty_shouldReturnAllGroupsByGivenFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> firstGroupSubjects = new ArrayList<>();
        firstGroupSubjects.add(new Subject(1, "Java", "Learn Java"));
        firstGroupSubjects.add(new Subject(2, "Sql", "Learn Sql"));
        List<Subject> secondGroupSubjects = new ArrayList<>();
        secondGroupSubjects.add(new Subject(3, "Spring", "Learn Spring"));
        secondGroupSubjects.add(new Subject(4, "Hibernate", "Learn Hibernate"));

        List<Group> expected = new ArrayList<>();
        expected.add(new Group(1, "AA-111", firstGroupSubjects, faculty));
        expected.add(new Group(2, "BB-222", secondGroupSubjects, faculty));
        expected.add(new Group(3, "CC-333", new ArrayList<>(), faculty));
        expected.add(new Group(4, "DD-444", new ArrayList<>(), faculty));
        List<Group> actual = groupDao.getByFaculty(faculty);
        assertEquals(expected, actual);
    }

    @Test
    void removeLectureFromGroup_shouldRemoveRecordFromGroupsLectures() {
        Lecture lecture = lectureDao.getById(1).get();
        Group group = groupDao.getById(1).get();
        groupDao.removeLectureFromGroup(lecture, group);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "groups_lectures", "group_id=1 and lecture_id=1"));
    }
}
