package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.ClassRoomDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ClassRoomServiceImplTest {

    @Mock
    private ClassRoomDao classRoomDao;
    @Mock
    private LectureDao lectureDao;
    @InjectMocks
    private ClassRoomServiceImpl classRoomService;

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenClassRoomFacultyIsNull() {
        ClassRoom classRoom = new ClassRoom(null, 1, 100, null);
        assertThrows(InvalidEntityFieldException.class, () -> classRoomService.add(classRoom));
    }

    @Test
    void add_shouldThrowEntityIsAlreadyExistsException_whenClassRomIsAlreadyExists() {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(1, 1, 100, new Faculty(1, "Programming")));
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(classRooms);
        ClassRoom classRoom = new ClassRoom(null, 1, 100, new Faculty(null, "Name"));
        assertThrows(EntityIsAlreadyExistsException.class, () -> classRoomService.add(classRoom));
    }

    @Test
    void add_shouldReturnNewClassRoom_whenAddNewClassRoom() throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(1, 1, 100, new Faculty(1, "Programming")));
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(classRooms);
        ClassRoom classRoom = new ClassRoom(null, 1, 101, new Faculty(null, "Name"));
        ClassRoom expected = new ClassRoom(4, 1, 101, new Faculty(3, "Name"));
        when(classRoomDao.add(classRoom)).thenReturn(expected);
        ClassRoom actual = classRoomService.add(classRoom);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllClassRooms() {
        Faculty faculty = new Faculty(1, "Programming");
        List<ClassRoom> expected = new ArrayList<>();
        expected.add(new ClassRoom(1, 1, 100, faculty));
        expected.add(new ClassRoom(2, 2, 200, faculty));
        expected.add(new ClassRoom(3, 3, 300, faculty));
        when(classRoomDao.getAll()).thenReturn(expected);
        List<ClassRoom> actual = classRoomService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectClassRoomByGivenId() throws EntityNotFoundException {
        ClassRoom expected = new ClassRoom(1, 1, 100, new Faculty(1, "Programming"));
        when(classRoomDao.getById(1)).thenReturn(Optional.of(expected));
        ClassRoom actual = classRoomService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenClassRoomNotExist() {
        when(classRoomDao.getById(4)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> classRoomService.getById(4));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenClassRoomNotExist() {
        ClassRoom classRoom = new ClassRoom(null, 4, 400, null);
        assertThrows(EntityNotFoundException.class, () -> classRoomService.update(classRoom));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenClassRoomFacultyIsNull() {
        ClassRoom classRoomForUpdate = new ClassRoom(1, 1, 200, null);
        assertThrows(InvalidEntityFieldException.class, () -> classRoomService.update(classRoomForUpdate));
    }

    @Test
    void update_shouldThrowEntityIsAlreadyExistsException_whenClassRoomIsAlreadyExists() {
        List<ClassRoom> classRooms = new ArrayList<>();
        ClassRoom classRoom = new ClassRoom(1, 1, 100, new Faculty(1, "Programming"));
        classRooms.add(classRoom);
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(classRooms);
        assertThrows(EntityIsAlreadyExistsException.class, () -> classRoomService.update(classRoom));
    }

    @Test
    void update_shouldCorrectlyUpdateClassRoom() throws EntityIsAlreadyExistsException, EntityNotFoundException, InvalidEntityFieldException {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(1, 1, 100, new Faculty(1, "Programming")));
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(classRooms);
        ClassRoom classRoom = new ClassRoom(1, 1, 110, new Faculty(1, "Programming"));
        classRoomService.update(classRoom);
        verify(classRoomDao, times(1)).getByBuildingNumber(1);
        verify(classRoomDao, times(1)).update(classRoom);
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenClassRoomHasLectures() {
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));

        List<Lecture> lectures = Arrays.asList(new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00)));
        when(classRoomDao.getById(1)).thenReturn(Optional.of(classRoom));
        when(lectureDao.getByClassRoom(classRoom)).thenReturn(lectures);
        assertThrows(EntityHasReferenceException.class, () -> classRoomService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenClassRoomNotExist() {
        when(classRoomDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> classRoomService.deleteById(7));
    }

    @Test
    void deleteById_shouldDeleteClassroom_whenClassRoomHasNoLectures() throws EntityHasReferenceException, EntityNotFoundException {
        ClassRoom classRoom = new ClassRoom(3, 3, 300, new Faculty(1, "Programming"));
        when(classRoomDao.getById(3)).thenReturn(Optional.of(classRoom));
        when(lectureDao.getByClassRoom(classRoom)).thenReturn(new ArrayList<>());
        when(classRoomDao.deleteById(3)).thenReturn(true);
        boolean isDeleted = classRoomService.deleteById(3);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldCorrectlyAddAllClassRooms() throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(1, 1, 100, new Faculty(1, "Programming")));
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(classRooms);

        List<ClassRoom> classRoomsForAddAll = new ArrayList<>();
        classRoomsForAddAll.add(new ClassRoom(null, 1, 150, new Faculty(null, "Name")));
        classRoomService.addAll(classRoomsForAddAll);
        verify(classRoomDao, times(1)).addAll(classRoomsForAddAll);
    }

    @Test
    void addAll_shouldThrowEntityIsAlreadyExistsException_whenClassRomIsAlreadyExists() {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(1, 1, 100, new Faculty(1, "Programming")));
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(classRooms);
        List<ClassRoom> classRoomsForAddAll = new ArrayList<>();
        classRoomsForAddAll.add(new ClassRoom(null, 1, 100, new Faculty(null, "Name")));
        assertThrows(EntityIsAlreadyExistsException.class, () -> classRoomService.addAll(classRoomsForAddAll));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenClassRoomFacultyIsNull() {
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(null, 1, 150, null));
        assertThrows(InvalidEntityFieldException.class, () -> classRoomService.addAll(classRooms));
    }

    @Test
    void getByBuildingNumber_shouldReturnClassRoomsWithGivenBuildingNumber() {
        List<ClassRoom> expected = new ArrayList<>();
        expected.add(new ClassRoom(1, 1, 100, new Faculty(1, "Programming")));
        when(classRoomDao.getByBuildingNumber(1)).thenReturn(expected);
        List<ClassRoom> actual = classRoomService.getByBuildingNumber(1);
        assertEquals(expected, actual);
    }
}