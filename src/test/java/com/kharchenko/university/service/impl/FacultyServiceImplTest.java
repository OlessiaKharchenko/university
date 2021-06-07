package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.ClassRoomDao;
import com.kharchenko.university.dao.FacultyDao;
import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EnitityAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Schedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
class FacultyServiceImplTest {

    @Mock
    private FacultyDao facultyDao;
    @Mock
    private ClassRoomDao classRoomDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private ScheduleDao scheduleDao;
    @InjectMocks
    private FacultyServiceImpl facultyService;

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenFacultyNameIsNull() {
        Faculty faculty = new Faculty(null, null);
        assertThrows(InvalidEntityFieldException.class, () -> facultyService.add(faculty));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenFacultyNameIsEmpty() {
        Faculty faculty = new Faculty(null, "");
        assertThrows(InvalidEntityFieldException.class, () -> facultyService.add(faculty));
    }

    @Test
    void add_shouldReturnNewFaculty_whenAddNewFaculty() {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty(1, "Programming"));
        faculties.add(new Faculty(2, "Management"));
        when(facultyDao.getAll()).thenReturn(faculties);

        Faculty faculty = new Faculty(null, "New Faculty");
        Faculty expected = new Faculty(3, "New Faculty");
        when(facultyDao.add(faculty)).thenReturn(expected);
        Faculty actual = facultyService.add(faculty);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllFaculties() {
        List<Faculty> expected = new ArrayList<>();
        expected.add(new Faculty(1, "Programming"));
        expected.add(new Faculty(2, "Management"));
        when(facultyDao.getAll()).thenReturn(expected);
        List<Faculty> actual = facultyService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectFacultyByGivenId() {
        Faculty expected = new Faculty(1, "Programming");
        when(facultyDao.getById(1)).thenReturn(Optional.of(expected));
        Faculty actual = facultyService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenFacultyNotExist() {
        when(facultyDao.getById(3)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> facultyService.getById(3));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenFacultyNotExist() {
        Faculty faculty = new Faculty(null, "Programming");
        assertThrows(EntityNotFoundException.class, () -> facultyService.update(faculty));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenFacultyNameIsNull() {
        Faculty faculty = new Faculty(1, null);
        assertThrows(InvalidEntityFieldException.class, () -> facultyService.update(faculty));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenFacultyNameIsEmpty() {
        Faculty faculty = new Faculty(1, "");
        assertThrows(InvalidEntityFieldException.class, () -> facultyService.update(faculty));
    }

    @Test
    void update_shouldThrowEntityIsAlreadyExistsException_whenFacultyIsAlreadyExists() {
        Faculty firstFaculty = new Faculty(1, "Programming");
        Faculty secondFaculty = new Faculty(2, "Management");
        List<Faculty> faculties = Arrays.asList(firstFaculty, secondFaculty);
        when(facultyDao.getAll()).thenReturn(faculties);
        Faculty faculty = new Faculty(1, "Programming");
        assertThrows(EnitityAlreadyExistsException.class, () -> facultyService.update(faculty));
    }

    @Test
    void update_shouldCorrectlyUpdateFaculty() {
        Faculty firstFaculty = new Faculty(1, "Programming");
        Faculty secondFaculty = new Faculty(2, "Management");
        List<Faculty> faculties = Arrays.asList(firstFaculty, secondFaculty);
        when(facultyDao.getAll()).thenReturn(faculties);
        Faculty faculty = new Faculty(1, "Updated Faculty");
        facultyService.update(faculty);
        verify(facultyDao, times(1)).getAll();
        verify(facultyDao, times(1)).update(faculty);
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenFacultyHasClassRooms() {
        Faculty faculty = new Faculty(1, "Programming");
        List<ClassRoom> classRooms = new ArrayList<>();
        classRooms.add(new ClassRoom(1, 1, 100, faculty));
        classRooms.add(new ClassRoom(2, 2, 200, faculty));
        when(facultyDao.getById(1)).thenReturn(Optional.of(faculty));
        when(classRoomDao.getByFaculty(faculty)).thenReturn(classRooms);
        assertThrows(EntityHasReferenceException.class, () -> facultyService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenFacultyHasSchedules() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Lecture> lectures = new ArrayList<>();
        lectures.add(new Lecture(1, null, null, null, null,
                null, null));
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(new Schedule(1, lectures, LocalDate.of(2021, 05, 24), faculty));
        when(facultyDao.getById(1)).thenReturn(Optional.of(faculty));
        when(scheduleDao.getByFaculty(faculty)).thenReturn(schedules);
        assertThrows(EntityHasReferenceException.class, () -> facultyService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenFacultyHasGroups() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", subjects, faculty));
        when(facultyDao.getById(1)).thenReturn(Optional.of(faculty));
        when(groupDao.getByFaculty(faculty)).thenReturn(groups);
        assertThrows(EntityHasReferenceException.class, () -> facultyService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenFacultyNotExist() {
        when(facultyDao.getById(3)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> facultyService.deleteById(3));
    }

    @Test
    void deleteById_shouldDeleteFaculty_whenFacultyHasNoReferences() {
        Faculty faculty = new Faculty(2, "Management");
        when(facultyDao.getById(2)).thenReturn(Optional.of(faculty));
        when(classRoomDao.getByFaculty(faculty)).thenReturn(new ArrayList<>());
        when(scheduleDao.getByFaculty(faculty)).thenReturn(new ArrayList<>());
        when(groupDao.getByFaculty(faculty)).thenReturn(new ArrayList<>());
        when(facultyDao.deleteById(2)).thenReturn(true);
        boolean isDeleted = facultyService.deleteById(2);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenFacultyNameIsNull() {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty(null, null));
        assertThrows(InvalidEntityFieldException.class, () -> facultyService.addAll(faculties));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenFacultyNameIsEmpty() {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty(null, ""));
        assertThrows(InvalidEntityFieldException.class, () -> facultyService.addAll(faculties));
    }

    @Test
    void addAll_shouldThrowEntityIsAlreadyExistsException_whenFacultyIsAlreadyExists() {
        List<Faculty> expected = new ArrayList<>();
        expected.add(new Faculty(1, "Programming"));
        expected.add(new Faculty(2, "Management"));
        when(facultyDao.getAll()).thenReturn(expected);
        List<Faculty> facultiesForAddAll = Arrays.asList(new Faculty(null, "Programming"));
        assertThrows(EnitityAlreadyExistsException.class, () -> facultyService.addAll(facultiesForAddAll));
    }

    @Test
    void addAll_shouldCorrectlyAddAllSubjects() {
        List<Faculty> expected = new ArrayList<>();
        expected.add(new Faculty(1, "Programming"));
        expected.add(new Faculty(2, "Management"));
        when(facultyDao.getAll()).thenReturn(expected);
        List<Faculty> facultiesForAddAll = Arrays.asList(new Faculty(null, "New Faculty"));
        facultyService.addAll(facultiesForAddAll);
        verify(facultyDao, times(1)).addAll(facultiesForAddAll);
    }
}