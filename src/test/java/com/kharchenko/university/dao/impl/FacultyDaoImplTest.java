package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FacultyDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FacultyDaoImpl facultyDao;


    @Test
    void add_shouldReturnNewFaculty_whenAddNewFaculty() {
        Faculty faculty = new Faculty(null, "Foreign languages");
        Faculty actual = facultyDao.add(faculty);
        assertEquals(new Faculty(3, "Foreign languages"), actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewFaculty() {
        Faculty faculty = new Faculty(null, "Foreign languages");
        facultyDao.add(faculty);
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "faculties"));
    }

    @Test
    void getById_shouldReturnCorrectFacultyByGivenId() {
        Faculty expected = new Faculty(1, "Programming");
        Faculty actual = facultyDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenFacultyNotExist() {
        Optional<Faculty> expected = Optional.empty();
        Optional<Faculty> actual = facultyDao.getById(3);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllFaculties() {
        List<Faculty> expected = new ArrayList<>();
        expected.add(new Faculty(1, "Programming"));
        expected.add(new Faculty(2, "Management"));
        List<Faculty> actual = facultyDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnCorrectFacultyRecordsCount() {
        int facultiesSize = facultyDao.getAll().size();
        assertEquals(facultiesSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "faculties"));
    }

    @Test
    void update_shouldCorrectlyUpdateFacultyRecord() {
        Faculty faculty = new Faculty(1, "Computer Science");
        facultyDao.update(faculty);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "faculties", "faculty_id=1 and faculty_name='Computer Science'"));
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheFaculty() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            facultyDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteFacultyRecord_whenReferencesToTheFacultyNotExists() {
        facultyDao.deleteById(2);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "faculties"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "faculties",
                "faculty_id=2"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListFaculties() {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add( new Faculty(null, "First"));
        faculties.add( new Faculty(null, "Second"));
        facultyDao.addAll(faculties);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "faculties"));
    }
}
