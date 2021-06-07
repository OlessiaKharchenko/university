package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.SubjectDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EnitityAlreadyExistsException;
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
class SubjectServiceImplTest {

    @Mock
    private SubjectDao subjectDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private LectureDao lectureDao;
    @InjectMocks
    private SubjectServiceImpl subjectService;


    @Test
    void add_shouldThrowInvalidEntityFieldException_whenSubjectNameIsNull() {
        Subject subject = new Subject(null, null, "Learn Jdbc");
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.add(subject));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenSubjectNameIsEmpty() {
        Subject subject = new Subject(null, "", "Learn Jdbc");
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.add(subject));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenSubjectDescriptionIsNull() {
        Subject subject = new Subject(null, "Jdbc", null);
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.add(subject));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenSubjectDescriptionIsEmpty() {
        Subject subject = new Subject(null, "Jdbc", "");
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.add(subject));
    }

    @Test
    void add_shouldThrowEntityIsAlreadyExistsException_whenSubjectIsAlreadyExists() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        subjects.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        subjects.add(new Subject(5, "Junit", "Learn Junit"));
        when(subjectDao.getAll()).thenReturn(subjects);
        Subject subject = new Subject(null, "Java", "Learn Java");
        assertThrows(EnitityAlreadyExistsException.class, () -> subjectService.add(subject));
    }

    @Test
    void add_shouldReturnNewSubject_whenAddNewSubject() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        subjects.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        subjects.add(new Subject(5, "Junit", "Learn Junit"));
        when(subjectDao.getAll()).thenReturn(subjects);

        Subject subject = new Subject(null, "Jdbc", "Learn Jdbc");
        Subject expected = new Subject(6, "Jdbc", "Learn Jdbc");
        when(subjectDao.add(subject)).thenReturn(expected);
        Subject actual = subjectService.add(subject);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllSubjects() {
        List<Subject> expected = new ArrayList<>();
        expected.add(new Subject(1, "Java", "Learn Java"));
        expected.add(new Subject(2, "Sql", "Learn Sql"));
        expected.add(new Subject(3, "Spring", "Learn Spring"));
        expected.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        expected.add(new Subject(5, "Junit", "Learn Junit"));
        when(subjectDao.getAll()).thenReturn(expected);
        List<Subject> actual = subjectService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectSubjectByGivenId() {
        Subject expected = new Subject(1, "Java", "Learn Java");
        when(subjectDao.getById(1)).thenReturn(Optional.of(expected));
        Subject actual = subjectService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenSubjectNotExist() {
        when(subjectDao.getById(6)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> subjectService.getById(6));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenSubjectNotExist() {
        Subject subject = new Subject(null, "Jdbc", "Learn Jdbc");
        assertThrows(EntityNotFoundException.class, () -> subjectService.update(subject));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenSubjectNameIsNull() {
        Subject subject = new Subject(1, null, "Learn Java");
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.update(subject));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenSubjectNameIsEmpty() {
        Subject subject = new Subject(1, "", "Learn Java");
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.update(subject));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenSubjectDescriptionIsNull() {
        Subject subject = new Subject(1, "Java", null);
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.update(subject));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenSubjectDescriptionIsEmpty() {
        Subject subject = new Subject(1, "Java", "");
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.update(subject));
    }

    @Test
    void update_shouldThrowEntityIsAlreadyExistsException_whenSubjectIsAlreadyExists() {
        List<Subject> expected = new ArrayList<>();
        expected.add(new Subject(1, "Java", "Learn Java"));
        expected.add(new Subject(2, "Sql", "Learn Sql"));
        when(subjectDao.getAll()).thenReturn(expected);
        Subject subject = new Subject(1, "Java", "Learn Java8");
        assertThrows(EnitityAlreadyExistsException.class, () -> subjectService.update(subject));
    }

    @Test
    void update_shouldCorrectlyUpdateSubject() {
        List<Subject> expected = new ArrayList<>();
        expected.add(new Subject(1, "Java", "Learn Java"));
        expected.add(new Subject(2, "Sql", "Learn Sql"));
        when(subjectDao.getAll()).thenReturn(expected);
        Subject subject = new Subject(1, "Java8", "Learn Java8");
        subjectService.update(subject);
        verify(subjectDao, times(1)).getAll();
        verify(subjectDao, times(1)).update(subject);
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenSubjectNotExist() {
        when(subjectDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> subjectService.deleteById(7));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenSubjectIsLearntByGroups() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));

        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", subjects, faculty));
        Subject subject = new Subject(1, "Java", "Learn Java");
        when(subjectDao.getById(1)).thenReturn(Optional.of(subject));
        when(groupDao.getBySubject(subject)).thenReturn(groups);
        assertThrows(EntityHasReferenceException.class, () -> subjectService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenSubjectIsTaughtByTeachers() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        List<Teacher> teachers = Arrays.asList(new Teacher(1, "Bruce", "Eckel", subjects));
        Subject subject = new Subject(1, "Java", "Learn Java");
        when(subjectDao.getById(1)).thenReturn(Optional.of(subject));
        when(teacherDao.getBySubject(subject)).thenReturn(teachers);
        assertThrows(EntityHasReferenceException.class, () -> subjectService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenSubjectIsTaughtOnLectures() {
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));

        List<Lecture> lectures = Arrays.asList(new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00)));
        when(subjectDao.getById(1)).thenReturn(Optional.of(subject));
        when(lectureDao.getBySubject(subject)).thenReturn(lectures);
        assertThrows(EntityHasReferenceException.class, () -> subjectService.deleteById(1));
    }

    @Test
    void deleteById_shouldDeleteSubject_whenSubjectHasNoReferences() {
        Subject subject = new Subject(5, "Junit", "Learn Junit");
        when(subjectDao.getById(5)).thenReturn(Optional.of(subject));
        when(groupDao.getBySubject(subject)).thenReturn(new ArrayList<>());
        when(teacherDao.getBySubject(subject)).thenReturn(new ArrayList<>());
        when(lectureDao.getBySubject(subject)).thenReturn(new ArrayList<>());
        when(subjectDao.deleteById(5)).thenReturn(true);
        boolean isDeleted = subjectService.deleteById(5);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenSubjectNameIsNull() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(null, null, "Learn Jdbc"));
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.addAll(subjects));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenSubjectNameIsEmpty() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(null, "", "Learn Jdbc"));
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.addAll(subjects));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenSubjectDescriptionIsNull() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(null, "Jdbc", null));
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.addAll(subjects));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenSubjectDescriptionIsEmpty() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(null, "Jdbc", null));
        assertThrows(InvalidEntityFieldException.class, () -> subjectService.addAll(subjects));
    }

    @Test
    void addAll_shouldThrowEntityIsAlreadyExistsException_whenSubjectIsAlreadyExists() {
        List<Subject> expected = new ArrayList<>();
        expected.add(new Subject(1, "Java", "Learn Java"));
        expected.add(new Subject(2, "Sql", "Learn Sql"));
        expected.add(new Subject(3, "Spring", "Learn Spring"));
        expected.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        expected.add(new Subject(5, "Junit", "Learn Junit"));
        when(subjectDao.getAll()).thenReturn(expected);
        List<Subject> subjectsForAddAll = Arrays.asList(new Subject(null, "Java", "Learn Java"));
        assertThrows(EnitityAlreadyExistsException.class, () -> subjectService.addAll(subjectsForAddAll));
    }

    @Test
    void addAll_shouldCorrectlyAddAllSubjects() {
        List<Subject> expected = new ArrayList<>();
        expected.add(new Subject(1, "Java", "Learn Java"));
        expected.add(new Subject(2, "Sql", "Learn Sql"));
        expected.add(new Subject(3, "Spring", "Learn Spring"));
        expected.add(new Subject(4, "Hibernate", "Learn Hibernate"));
        expected.add(new Subject(5, "Junit", "Learn Junit"));
        when(subjectDao.getAll()).thenReturn(expected);
        List<Subject> subjectsForAddAll = Arrays.asList(new Subject(null, "Java8", "Learn Java8"));
        subjectService.addAll(subjectsForAddAll);
        verify(subjectDao, times(1)).addAll(subjectsForAddAll);
    }

    @Test
    void addTeacherToSubject_shouldCorrectlyAddTeacherToSubject() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", subjects);
        Subject subject = new Subject(2, "Sql", "Learn Sql");
        subjectService.addTeacherToSubject(subject, teacher);
        verify(subjectDao, times(1)).addTeacherToSubject(subject, teacher);
    }

    @Test
    void addSubjectToGroup_shouldCorrectlyAddSubjectToGroup() {
        Subject subject = new Subject(3, "Spring", "Learn Spring");
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "AA-111", subjects, faculty);
        subjectService.addSubjectToGroup(subject, group);
        verify(subjectDao, times(1)).addSubjectToGroup(subject, group);
    }

    @Test
    void removeSubjectFromGroup_shouldCorrectlyRemoveSubjectFromGroup() {
        Faculty faculty = new Faculty(1, "Programming");
        Subject subject = new Subject(3, "Spring", "Learn Spring");
        List<Subject> subjects = Arrays.asList(subject);
        Group group = new Group(1, "AA-111", subjects, faculty);
        subjectService.removeSubjectFromGroup(subject, group);
        verify(subjectDao, times(1)).removeSubjectFromGroup(subject, group);
    }

    @Test
    void removeSubjectFromTeacher_shouldCorrectlyRemoveSubjectFromTeacher() {
        Subject subject = new Subject(2, "Sql", "Learn Sql");
        List<Subject> subjects = Arrays.asList(subject);
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", subjects);
        subjectService.removeSubjectFromTeacher(subject, teacher);
        verify(subjectDao, times(1)).removeSubjectFromTeacher(subject, teacher);
    }
}
