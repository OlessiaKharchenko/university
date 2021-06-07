package com.kharchenko.university.dao.impl;

import com.kharchenko.university.config.TestDaoConfig;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoConfig.class}, loader = AnnotationConfigContextLoader.class)
@SqlGroup({@Sql("classpath:create_tables.sql"), @Sql("classpath:test_data.sql")})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClassRoomDaoImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ClassRoomDaoImpl classroomDao;

    @Test
    void add_shouldReturnNewClassRoom_whenAddNewClassRoom() {
        ClassRoom classRoom = new ClassRoom(null, 4, 400, new Faculty(null, "Name"));
        ClassRoom actual = classroomDao.add(classRoom);
        assertEquals(new ClassRoom(4, 4, 400, new Faculty(3, "Name")), actual);
    }

    @Test
    void add_shouldInsertNewRecord_whenAddNewClassRoom() {
        ClassRoom classRoom = new ClassRoom(null, 4, 400, new Faculty(1, "Programming"));
        classroomDao.add(classRoom);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "classrooms"));
        assertEquals(4, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "classrooms", "faculty_id=1"));
    }

    @Test
    void getById_shouldReturnCorrectClassRoomByGivenId() {
        ClassRoom expected = new ClassRoom(1, 1, 100, new Faculty(1, "Programming"));
        ClassRoom actual = classroomDao.getById(1).get();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnEmpty_whenClassRoomNotExist() {
        Optional<ClassRoom> expected = Optional.empty();
        Optional<ClassRoom> actual = classroomDao.getById(4);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllClassRooms() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom firstClassRoom = new ClassRoom(1, 1, 100, faculty);
        ClassRoom secondClassRoom = new ClassRoom(2, 2, 200, faculty);
        ClassRoom thirdClassRoom = new ClassRoom(3, 3, 300, faculty);
        List<ClassRoom> expected = Arrays.asList(firstClassRoom, secondClassRoom, thirdClassRoom);
        List<ClassRoom> actual = classroomDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnCorrectClassRoomRecordsCount() {
        int classRoomsSize = classroomDao.getAll().size();
        assertEquals(classRoomsSize, JdbcTestUtils.countRowsInTable(jdbcTemplate, "classrooms"));
    }

    @Test
    void update_shouldCorrectlyUpdateClassRoomRecord() {
        Faculty faculty = new Faculty(2, "Management");
        ClassRoom classRoom = new ClassRoom(1, 4, 400, faculty);
        classroomDao.update(classRoom);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "classrooms", "classroom_id=1 and building_number=4 and room_number=400" +
                        "and faculty_id=2"));
    }

    @Test
    void getByBuildingNumber_shouldReturnAllClassRoomsWithGivenBuildingNumber() {
        Faculty faculty = new Faculty(1, "Programming");
        List<ClassRoom> expected = new ArrayList<>();
        expected.add(new ClassRoom(1, 1, 100, faculty));
        List<ClassRoom> actual = classroomDao.getByBuildingNumber(1);
        assertEquals(expected, actual);
    }

    @Test
    void deleteById_shouldThrowException_whenExistReferencesToTheClassroom() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            classroomDao.deleteById(1);
        });
    }

    @Test
    void deleteById_shouldDeleteClassroomRecord_whenReferencesToTheClassroomNotExists() {
        classroomDao.deleteById(3);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "classrooms"));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "classrooms",
                "classroom_id=3"));
    }

    @Test
    void addAll_shouldInsertNewRecords_whenAddListClassRooms() {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(null, 1, 150, new Faculty(null, "Name")));
        classroomDao.addAll(classRooms);
        assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, "classrooms"));
    }

    @Test
    void getByFaculty_shouldReturnAllClassRoomsByGivenFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom firstClassRoom = new ClassRoom(1, 1, 100, faculty);
        ClassRoom secondClassRoom = new ClassRoom(2, 2, 200, faculty);
        ClassRoom thirdClassRoom = new ClassRoom(3, 3, 300, faculty);
        List<ClassRoom> expected = Arrays.asList(firstClassRoom, secondClassRoom, thirdClassRoom);
        List<ClassRoom> actual = classroomDao.getByFaculty(faculty);
        assertEquals(expected, actual);
    }
}