package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.StudentDao;
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
import com.kharchenko.university.model.Student;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupDao groupDao;
    @Mock
    private StudentDao studentDao;
    @Mock
    private LectureDao lectureDao;
    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenGroupNameIsNull() {
        Group group = new Group(null, null, null, new Faculty(null, "Name"));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.add(group));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenGroupNameIsEmpty() {
        Group group = new Group(null, "", null, new Faculty(null, "Name"));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.add(group));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenGroupFacultyIsNull() {
        Group group = new Group(null, "New group", null, null);
        assertThrows(InvalidEntityFieldException.class, () -> groupService.add(group));
    }

    @Test
    void add_shouldReturnNewGroup_whenAddNewGroup() throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        Faculty faculty = new Faculty(1, "Programming");
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));
        when(groupDao.getAll()).thenReturn(groups);
        Group group = new Group(null, "New group", null, new Faculty(null, "Name"));
        Group expected = new Group(3, "New group", null, new Faculty(null, "Name"));
        when(groupDao.add(group)).thenReturn(expected);
        Group actual = groupService.add(group);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllGroups() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Group> expected = new ArrayList<>();
        expected.add(new Group(1, "AA-111", null, faculty));
        expected.add(new Group(2, "BB-222", null, faculty));
        when(groupDao.getAll()).thenReturn(expected);
        List<Group> actual = groupService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectGroupByGivenId() throws EntityNotFoundException {
        Group expected = new Group(1, "A-11", new ArrayList<>(), new Faculty(null, "Name"));
        when(groupDao.getById(1)).thenReturn(Optional.of(expected));
        Group actual = groupService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenGroupNotExist() {
        when(groupDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> groupService.getById(7));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenGroupNotExist() {
        Group group = new Group(null, "A-11", new ArrayList<>(), new Faculty(null, "Name"));
        assertThrows(EntityNotFoundException.class, () -> groupService.update(group));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenGroupNameIsNull() {
        Group group = new Group(1, null, null, new Faculty(null, "Name"));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.update(group));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenGroupNameIsEmpty() {
        Group group = new Group(1, "", null, new Faculty(null, "Name"));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.update(group));
    }

    @Test
    void update_shouldThrowEntityIsAlreadyExistsException_whenGroupIsAlreadyExists() {
        List<Group> groups = new ArrayList<>();
        Group group = new Group(1, "A-11", new ArrayList<>(), new Faculty(null, "Name"));
        groups.add(group);
        when(groupDao.getAll()).thenReturn(groups);
        assertThrows(EntityIsAlreadyExistsException.class, () -> groupService.update(group));
    }

    @Test
    void update_shouldCorrectlyUpdateGroup() throws EntityNotFoundException, InvalidEntityFieldException, EntityIsAlreadyExistsException {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "A-11", new ArrayList<>(), new Faculty(null, "Name")));
        when(groupDao.getAll()).thenReturn(groups);
        Group group = new Group(1, "A-12", null, new Faculty(null, "Name"));
        groupService.update(group);
        verify(groupDao, times(1)).getAll();
        verify(groupDao, times(1)).update(group);
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenGroupNotExist() {
        when(groupDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> groupService.deleteById(7));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenGroupHasLectures() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        Group group = new Group(1, "A-11", new ArrayList<>(), new Faculty(null, "Name"));
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00));
        List<Lecture> lectures = Arrays.asList(lecture);
        when(groupDao.getById(1)).thenReturn(Optional.of(group));
        when(lectureDao.getGroupLectures(group)).thenReturn(lectures);
        assertThrows(EntityHasReferenceException.class, () -> groupService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenGroupHasStudents() {
        Group group = new Group(1, "A-11", new ArrayList<>(), new Faculty(null, "Name"));
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "Ivan", "Ivanov", group));
        students.add(new Student(2, "Petr", "Petrov", group));
        when(groupDao.getById(1)).thenReturn(Optional.of(group));
        when(studentDao.getGroupStudents(group)).thenReturn(students);
        assertThrows(EntityHasReferenceException.class, () -> groupService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenGroupHasSubjects() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "A-11", subjects, new Faculty(null, "Name"));
        when(groupDao.getById(1)).thenReturn(Optional.of(group));
        assertThrows(EntityHasReferenceException.class, () -> groupService.deleteById(1));
    }

    @Test
    void deleteById_shouldDeleteGroup_whenGroupHasNoReferences() throws EntityHasReferenceException, EntityNotFoundException {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        when(groupDao.getById(1)).thenReturn(Optional.of(group));
        when(studentDao.getGroupStudents(group)).thenReturn(new ArrayList<>());
        when(lectureDao.getGroupLectures(group)).thenReturn(new ArrayList<>());
        when(groupDao.deleteById(1)).thenReturn(true);
        boolean isDeleted = groupService.deleteById(1);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenGroupNameIsNull() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(null, null, null, new Faculty(null, "Name")));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.addAll(groups));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenGroupNameIsEmpty() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(null, "", null, new Faculty(null, "Name")));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.addAll(groups));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenGroupFacultyIsNull() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(null, "New group", null, null));
        assertThrows(InvalidEntityFieldException.class, () -> groupService.addAll(groups));
    }

    @Test
    void addAll_shouldCorrectlyAddAllGroups() throws EntityIsAlreadyExistsException, InvalidEntityFieldException {
        Faculty faculty = new Faculty(1, "Programming");
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));
        when(groupDao.getAll()).thenReturn(groups);
        List<Group> subjectsForAddAll = Arrays.asList(new Group(null, "CC-333", null, faculty));
        groupService.addAll(subjectsForAddAll);
        verify(groupDao, times(1)).addAll(subjectsForAddAll);
    }

    @Test
    void addLectureToGroup_shouldCorrectlyAddLectureToGroup() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(2, 2, 200, faculty);
        Subject subject = new Subject(2, "Sql", "Learn Sql");
        List<Subject> subjects = Arrays.asList(subject);
        Teacher teacher = new Teacher(2, "Robert", "Martin", null);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(2, "BB-222", subjects, faculty));
        Lecture lecture = new Lecture(2, subject, teacher, classRoom, groups,
                LocalTime.of(12, 00), LocalTime.of(14, 00));
        Group group = new Group(3, "CC-333", subjects, faculty);
        groupService.addLectureToGroup(lecture, group);
        verify(groupDao, times(1)).addLectureToGroup(lecture, group);
    }

    @Test
    void removeLectureFromGroup_shouldCorrectlyRemoveLectureFromGroup() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(2, 2, 200, faculty);
        Subject subject = new Subject(2, "Sql", "Learn Sql");
        List<Subject> subjects = Arrays.asList(subject);
        Teacher teacher = new Teacher(2, "Robert", "Martin", null);
        Group group = new Group(2, "BB-222", subjects, faculty);
        List<Group> groups = Arrays.asList(group);
        Lecture lecture = new Lecture(2, subject, teacher, classRoom, groups,
                LocalTime.of(12, 00), LocalTime.of(14, 00));
        groupService.removeLectureFromGroup(lecture, group);
        verify(groupDao, times(1)).removeLectureFromGroup(lecture, group);
    }

    @Test
    void getBySubject_shouldReturnAllGroupsByGivenSubject() {
        Faculty faculty = new Faculty(1, "Programming");
        Subject firstSubject = new Subject(1, "Java", "Learn Java");
        Subject secondSubject = new Subject(2, "Sql", "Learn Sql");
        List<Subject> subjects = Arrays.asList(firstSubject, secondSubject);
        List<Group> expected = Arrays.asList(new Group(1, "AA-111", subjects, faculty));
        when(groupDao.getBySubject(firstSubject)).thenReturn(expected);
        List<Group> actual = groupService.getBySubject(firstSubject);
        assertEquals(expected, actual);
    }

    @Test
    void getByFaculty_shouldReturnAllGroupsByGivenFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        List<Group> expected = new ArrayList<>();
        expected.add(new Group(1, "AA-111", subjects, faculty));
        when(groupDao.getByFaculty(faculty)).thenReturn(expected);
        List<Group> actual = groupService.getByFaculty(faculty);
        assertEquals(expected, actual);
    }
}