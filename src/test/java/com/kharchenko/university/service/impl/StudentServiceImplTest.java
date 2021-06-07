package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.StudentDao;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Student;
import com.kharchenko.university.model.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class StudentServiceImplTest {

    @Mock
    private StudentDao studentDao;
    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenStudentFirstNameIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, null, "Ivanov", group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.add(student));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenStudentFirstNameIsEmpty() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "", "Ivanov", group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.add(student));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenStudentLastNameIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "Ivan", null, group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.add(student));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenStudentLastNameIsEmpty() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "Ivan", "", group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.add(student));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenStudentGroupIsNull() {
        Student student = new Student(1, "Ivan", "Ivanov", null);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.add(student));
    }

    @Test
    void add_shouldReturnNewStudent_whenAddNewStudent() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(null, "Ivan", "Ivanov", group);
        Student expected = new Student(7, "Ivan", "Ivanov", group);
        when(studentDao.add(student)).thenReturn(expected);
        Student actual = studentService.add(student);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllStudents() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        subjects.add(new Subject(2, "Sql", "Learn Sql"));
        Group group = new Group(1, "AA-111", subjects, faculty);
        List<Student> expected = new ArrayList<>();
        expected.add(new Student(1, "Ivan", "Ivanov", group));
        expected.add(new Student(2, "Petr", "Petrov", group));
        when(studentDao.getAll()).thenReturn(expected);
        List<Student> actual = studentService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectStudentByGivenId() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Java", "Learn Java"));
        Group group = new Group(1, "AA-111", subjects, faculty);
        Student expected = new Student(1, "Ivan", "Ivanov", group);
        when(studentDao.getById(1)).thenReturn(Optional.of(expected));
        Student actual = studentService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenStudentNotExist() {
        when(studentDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> studentService.getById(7));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenStudentNotExist() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(null, "Ivan", "Ivanov", group);
        assertThrows(EntityNotFoundException.class, () -> studentService.update(student));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenStudentFirstNameIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, null, "Ivanov", group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.update(student));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenStudentFirstNameIsEmpty() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "", "Ivanov", group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.update(student));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenStudentLastNameIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "Ivan", null, group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.update(student));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenStudentLastNameIsEmpty() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "Ivan", "", group);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.update(student));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenStudentGroupIsNull() {
        Student student = new Student(1, "Ivan", "Ivanov", null);
        assertThrows(InvalidEntityFieldException.class, () -> studentService.update(student));
    }

    @Test
    void update_shouldCorrectlyUpdateStudent() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "Ivan", "Ivanov", group);
        studentService.update(student);
        verify(studentDao, times(1)).update(student);
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenStudentNotExist() {
        when(studentDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> studentService.deleteById(7));
    }

    @Test
    void deleteById_shouldDeleteStudent_whenStudentExist() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        Student student = new Student(1, "Ivan", "ivanov", group);
        when(studentDao.getById(1)).thenReturn(Optional.of(student));
        when(studentDao.deleteById(1)).thenReturn(true);
        boolean isDeleted = studentService.deleteById(1);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenStudentFirstNameIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        List<Student> students = Arrays.asList(new Student(1, null, "Ivanov", group));
        assertThrows(InvalidEntityFieldException.class, () -> studentService.addAll(students));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenStudentFirstNameIsEmpty() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        List<Student> students = Arrays.asList(new Student(1, "", "Ivanov", group));
        assertThrows(InvalidEntityFieldException.class, () -> studentService.addAll(students));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenStudentLastNameIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        List<Student> students = Arrays.asList(new Student(1, "Ivan", null, group));
        assertThrows(InvalidEntityFieldException.class, () -> studentService.addAll(students));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenStudentLastNameIsEmpty() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        List<Student> students = Arrays.asList(new Student(1, "Ivan", "", group));
        assertThrows(InvalidEntityFieldException.class, () -> studentService.addAll(students));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenStudentGroupIsNull() {
        List<Student> students = Arrays.asList(new Student(1, "Ivan", "Ivanov", null));
        assertThrows(InvalidEntityFieldException.class, () -> studentService.addAll(students));
    }

    @Test
    void addAll_shouldCorrectlyAddAllStudents() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        List<Student> students = Arrays.asList(new Student(null, "Ivan", "Ivanov", group));
        studentService.addAll(students);
        verify(studentDao, times(1)).addAll(students);
    }

    @Test
    void getGroupStudents_shouldReturnAllStudentsByGivenGroup() {
        Faculty faculty = new Faculty(1, "Programming");
        Group group = new Group(1, "AA-111", new ArrayList<>(), faculty);
        List<Student> expected = new ArrayList<>();
        expected.add(new Student(1, "Ivan", "Ivanov", group));
        when(studentDao.getGroupStudents(group)).thenReturn(expected);
        List<Student> actual = studentService.getGroupStudents(group);
        assertEquals(expected, actual);
    }
}