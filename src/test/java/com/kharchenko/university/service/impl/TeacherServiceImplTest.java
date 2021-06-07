package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
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
class TeacherServiceImplTest {

    @Mock
    private TeacherDao teacherDao;
    @Mock
    private LectureDao lectureDao;
    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenTeacherFirstNameIsNull() {
        Teacher teacher = new Teacher(null, null, "Surname", null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.add(teacher));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenTeacherFirstNameIsEmpty() {
        Teacher teacher = new Teacher(null, "", "Surname", null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.add(teacher));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenTeacherLastNameIsNull() {
        Teacher teacher = new Teacher(null, "Name", null, null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.add(teacher));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenTeacherLastNameIsEmpty() {
        Teacher teacher = new Teacher(null, "Name", "", null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.add(teacher));
    }

    @Test
    void add_shouldReturnNewTeacher_whenAddNewTeacher() {
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        Teacher expected = new Teacher(4, "Name", "Surname", null);
        when(teacherDao.add(teacher)).thenReturn(expected);
        Teacher actual = teacherService.add(teacher);
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
        List<Subject> emptySubjects = new ArrayList<>();
        emptySubjects.add(new Subject(0, null, null));

        List<Teacher> expected = new ArrayList<>();
        expected.add(new Teacher(1, "Bruce", "Eckel", firstSubjects));
        expected.add(new Teacher(2, "Robert", "Martin", secondSubjects));
        expected.add(new Teacher(3, "James", "Gosling", emptySubjects));
        when(teacherDao.getAll()).thenReturn(expected);
        List<Teacher> actual = teacherService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectTeacherByGivenId() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        Teacher expected = new Teacher(1, "Bruce", "Eckel", subjects);
        when(teacherDao.getById(1)).thenReturn(Optional.of(expected));
        Teacher actual = teacherService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenTeacherNotExist() {
        when(teacherDao.getById(4)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> teacherService.getById(4));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenTeacherNotExist() {
        Teacher teacher = new Teacher(null, "Name", "Surname", null);
        assertThrows(EntityNotFoundException.class, () -> teacherService.update(teacher));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenTeacherFirstNameIsNull() {
        Teacher teacher = new Teacher(1, null, "Eckel", null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.update(teacher));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenTeacherFirstNameIsEmpty() {
        Teacher teacher = new Teacher(1, "", "Eckel", null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.update(teacher));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenTeacherLastNameIsNull() {
        Teacher teacher = new Teacher(1, "Bruce", null, null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.update(teacher));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenTeacherLastNameIsEmpty() {
        Teacher teacher = new Teacher(1, "Bruce", "", null);
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.update(teacher));
    }

    @Test
    void update_shouldCorrectlyUpdateTeacher() {
        Teacher teacher = new Teacher(1, "Neil", "Alishev", null);
        teacherService.update(teacher);
        verify(teacherDao, times(1)).update(teacher);
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenTeacherNotExist() {
        when(teacherDao.getById(4)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> teacherService.deleteById(4));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenTeacherHasLectures() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));

        Teacher teacher = new Teacher(1, "Bruce", "Eckel", subjects);
        Subject subject = new Subject(1, "Java", "Learn Java");
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom classRoom = new ClassRoom(1, 1, 100, faculty);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        groups.add(new Group(2, "BB-222", null, faculty));

        List<Lecture> lectures = Arrays.asList(new Lecture(1, subject, teacher, classRoom, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00)));
        when(teacherDao.getById(1)).thenReturn(Optional.of(teacher));
        when(lectureDao.getTeacherLectures(teacher)).thenReturn(lectures);
        assertThrows(EntityHasReferenceException.class, () -> teacherService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenTeacherHasSubjects() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", subjects);
        when(teacherDao.getById(1)).thenReturn(Optional.of(teacher));
        assertThrows(EntityHasReferenceException.class, () -> teacherService.deleteById(1));
    }

    @Test
    void deleteById_shouldDeleteTeacher_whenTeacherHasNoReferences() {
        Teacher teacher = new Teacher(3, "James", "Gosling", new ArrayList<>());
        when(teacherDao.getById(3)).thenReturn(Optional.of(teacher));
        when(lectureDao.getTeacherLectures(teacher)).thenReturn(new ArrayList<>());
        when(teacherDao.deleteById(3)).thenReturn(true);
        boolean isDeleted = teacherService.deleteById(3);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenTeacherFirstNameIsNull() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(null, null, "Surname", null));
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.addAll(teachers));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenTeacherFirstNameIsEmpty() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(null, "", "Surname", null));
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.addAll(teachers));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenTeacherLastNameIsNull() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(null, "Name", null, null));
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.addAll(teachers));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenTeacherLastNameIsEmpty() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(null, "Name", "", null));
        assertThrows(InvalidEntityFieldException.class, () -> teacherService.addAll(teachers));
    }

    @Test
    void addAll_shouldCorrectlyAddAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(null, "Name", "Surname", null));
        teacherService.addAll(teachers);
        verify(teacherDao, times(1)).addAll(teachers);
    }

    @Test
    void getBySubject_shouldReturnAllTeachersByGivenSubject() {
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(3, "Spring", "Learn Spring"));
        List<Teacher> expected = new ArrayList<>();
        expected.add(new Teacher(1, "Bruce", "Eckel", subjects));
        Subject subject = new Subject(1, "Java", "Learn Java");
        when(teacherDao.getBySubject(subject)).thenReturn(expected);
        List<Teacher> actual = teacherService.getBySubject(subject);
        assertEquals(expected, actual);
    }
}