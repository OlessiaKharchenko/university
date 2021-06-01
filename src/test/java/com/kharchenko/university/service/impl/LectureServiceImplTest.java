package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.exception.InvalidTeacherException;
import com.kharchenko.university.exception.InvalidGroupException;
import com.kharchenko.university.exception.InvalidClassRoomException;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Schedule;
import com.kharchenko.university.model.ClassRoom;
import com.kharchenko.university.model.Faculty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LectureServiceImplTest {

    @Mock
    private LectureDao lectureDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private ScheduleDao scheduleDao;
    @InjectMocks
    private LectureServiceImpl lectureService;

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureSubjectIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(null, null, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureTeacherIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(null, subject, null, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureClassRoomIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        Lecture lecture = new Lecture(null, subject, teacher, null, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureStartTimeIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(null, subject, teacher, classRoom, null, null,
                LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureEndTimeIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(null, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), null);
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowInvalidTeacherException_whenLectureTeacherIsNotQualified() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Subject teacherSubject = new Subject(null, "Sql", "Learn Sql");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(teacherSubject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(null, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidTeacherException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowInvalidGroupException_whenLectureGroupNotLearnSubject() {
        Faculty faculty = new Faculty(null, "Name");
        Subject subject = new Subject(null, "Java", "Learn Java");
        Subject groupSubject = new Subject(null, "Sql", "Learn Sql");
        Group group = new Group(null, "New group", Arrays.asList(groupSubject), faculty);
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, faculty);
        Lecture lecture = new Lecture(null, subject, teacher, classRoom, Arrays.asList(group),
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidGroupException.class, () -> lectureService.add(lecture));
    }

    @Test
    void add_shouldThrowEntityIsAlreadyExistsException_whenLectureIsAlreadyExists() {
        Lecture lectureWithId = getLectures().get(0);
        Lecture lectureToAdd = getLectures().get(1);
        when(teacherDao.getById(1)).thenReturn(Optional.of(lectureWithId.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lectureWithId.getGroups().get(0)));
        when(lectureDao.getAll()).thenReturn(Arrays.asList(lectureWithId));
        assertThrows(EntityIsAlreadyExistsException.class, () -> lectureService.add(lectureToAdd));
    }

    @Test
    void add_shouldReturnNewLecture_whenAddNewLecture() throws EntityIsAlreadyExistsException, InvalidEntityFieldException,
            InvalidGroupException, EntityNotFoundException, InvalidTeacherException {
        Lecture lectureWithId = getLectures().get(2);
        Lecture lectureToAdd = getLectures().get(3);
        when(teacherDao.getById(1)).thenReturn(Optional.of(lectureWithId.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lectureWithId.getGroups().get(0)));
        when(lectureDao.getAll()).thenReturn(Arrays.asList(getLectures().get(0)));
        when(lectureDao.add(lectureToAdd)).thenReturn(lectureWithId);
        Lecture actual = lectureService.add(lectureToAdd);
        assertEquals(lectureWithId, actual);
    }

    @Test
    void getAll_shouldReturnAllLecturesWithAllNestedEntities() throws EntityNotFoundException {
        Faculty faculty = new Faculty(1, "Programming");
        Subject firstSubject = new Subject(1, "Java", "Learn Java");
        Subject secondSubject = new Subject(3, "Spring", "Learn Spring");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", Arrays.asList(firstSubject, secondSubject));
        List<Subject> groupSubjects = Arrays.asList(firstSubject, secondSubject);
        Group group = new Group(1, "AA-111", groupSubjects, faculty);

        when(lectureDao.getAll()).thenReturn(getLecturesFromDao());
        when(teacherDao.getById(1)).thenReturn(Optional.of(teacher));
        when(groupDao.getById(1)).thenReturn(Optional.of(group));
        List<Lecture> expected = Arrays.asList(getLectures().get(0));
        List<Lecture> actual = lectureService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectLectureByGivenId() throws EntityNotFoundException {
        Lecture expected = getLectures().get(0);
        when(lectureDao.getById(1)).thenReturn(Optional.of(expected));
        when(teacherDao.getById(1)).thenReturn(Optional.of(expected.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(expected.getGroups().get(0)));
        Lecture actual = lectureService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenLectureNotExist() {
        when(lectureDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> lectureService.getById(7));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenLectureNotExist() {
        Lecture lecture = getLectures().get(1);
        assertThrows(EntityNotFoundException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureSubjectIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(1, null, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureTeacherIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(1, subject, null, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureClassRoomIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        Lecture lecture = new Lecture(1, subject, teacher, null, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureStartTimeIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, null, null,
                LocalTime.of(9, 00));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureEndTimeIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), null);
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidTeacherException_whenLectureTeacherIsNotQualified() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Subject teacherSubject = new Subject(null, "Sql", "Learn Sql");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(teacherSubject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidTeacherException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowInvalidGroupException_whenLectureGroupNotLearnSubject() {
        Faculty faculty = new Faculty(null, "Name");
        Subject subject = new Subject(null, "Java", "Learn Java");
        Subject groupSubject = new Subject(null, "Sql", "Learn Sql");
        Group group = new Group(null, "New group", Arrays.asList(groupSubject), faculty);
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, faculty);
        Lecture lecture = new Lecture(1, subject, teacher, classRoom, Arrays.asList(group),
                LocalTime.of(7, 00), LocalTime.of(9, 00));
        assertThrows(InvalidGroupException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldThrowEntityIsAlreadyExistsException_whenLectureIsAlreadyExists() {
        Lecture lecture = getLectures().get(0);
        when(teacherDao.getById(1)).thenReturn(Optional.of(lecture.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lecture.getGroups().get(0)));
        when(lectureDao.getAll()).thenReturn(Arrays.asList(lecture));
        assertThrows(EntityIsAlreadyExistsException.class, () -> lectureService.update(lecture));
    }

    @Test
    void update_shouldCorrectlyUpdateLecture() throws InvalidEntityFieldException, InvalidTeacherException, InvalidGroupException, EntityIsAlreadyExistsException, EntityNotFoundException {
        Lecture lecture = getLectures().get(0);
        Lecture lectureToUpdate = getLectures().get(2);
        when(teacherDao.getById(1)).thenReturn(Optional.of(lecture.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lecture.getGroups().get(0)));
        when(lectureDao.getAll()).thenReturn(Arrays.asList(lecture));
        lectureService.update(lectureToUpdate);
        verify(lectureDao, times(1)).getAll();
        verify(lectureDao, times(1)).update(lectureToUpdate);
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenLectureNotExist() {
        when(lectureDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> lectureService.deleteById(7));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenLectureUsedInSchedule() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Lecture lecture = getLectures().get(0);
        Schedule schedule = new Schedule(1, Arrays.asList(lecture), date, faculty);

        when(lectureDao.getById(1)).thenReturn(Optional.of(lecture));
        when(teacherDao.getById(1)).thenReturn(Optional.of(lecture.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lecture.getGroups().get(0)));
        when(scheduleDao.getByLecture(lecture)).thenReturn(Arrays.asList(schedule));
        assertThrows(EntityHasReferenceException.class, () -> lectureService.deleteById(1));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenLectureVisitedByGroups() {
        Lecture lecture = getLectures().get(0);
        when(lectureDao.getById(1)).thenReturn(Optional.of(lecture));
        when(teacherDao.getById(1)).thenReturn(Optional.of(lecture.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lecture.getGroups().get(0)));
        assertThrows(EntityHasReferenceException.class, () -> lectureService.deleteById(1));
    }

    @Test
    void deleteById_shouldDeleteLecture_whenLectureHasNoReferences() throws EntityHasReferenceException, EntityNotFoundException {
        Lecture lecture = getLectures().get(5);
        when(lectureDao.getById(1)).thenReturn(Optional.of(lecture));
        when(scheduleDao.getByLecture(lecture)).thenReturn(new ArrayList<>());
        when(teacherDao.getById(1)).thenReturn(Optional.of(lecture.getTeacher()));
        when(lectureDao.deleteById(1)).thenReturn(true);
        boolean isDeleted = lectureService.deleteById(1);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureSubjectIsNul() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        List<Lecture> lectures = Arrays.asList(new Lecture(null, null, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00)));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureTeacherIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        List<Lecture> lectures = Arrays.asList(new Lecture(null, subject, null, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00)));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureClassRoomIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        List<Lecture> lectures = Arrays.asList(new Lecture(null, subject, teacher, null, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00)));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureStartTimeIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        List<Lecture> lectures = Arrays.asList(new Lecture(null, subject, teacher, classRoom, null, null,
                LocalTime.of(9, 00)));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureEndTimeIsNull() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        List<Lecture> lectures = Arrays.asList(new Lecture(null, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), null));
        assertThrows(InvalidEntityFieldException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowInvalidTeacherException_whenLectureTeacherIsNotQualified() {
        Subject subject = new Subject(null, "Java", "Learn Java");
        Subject teacherSubject = new Subject(null, "Sql", "Learn Sql");
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(teacherSubject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, new Faculty(null, "Name"));
        List<Lecture> lectures = Arrays.asList(new Lecture(null, subject, teacher, classRoom, null,
                LocalTime.of(7, 00), LocalTime.of(9, 00)));
        assertThrows(InvalidTeacherException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowInvalidGroupException_whenLectureGroupNotLearnSubject() {
        Faculty faculty = new Faculty(null, "Name");
        Subject subject = new Subject(null, "Java", "Learn Java");
        Subject groupSubject = new Subject(null, "Sql", "Learn Sql");
        Group group = new Group(null, "New group", Arrays.asList(groupSubject), faculty);
        Teacher teacher = new Teacher(null, "Name", "Surname", Arrays.asList(subject));
        ClassRoom classRoom = new ClassRoom(null, 1, 130, faculty);
        List<Lecture> lectures = Arrays.asList(new Lecture(null, subject, teacher, classRoom, Arrays.asList(group),
                LocalTime.of(7, 00), LocalTime.of(9, 00)));
        assertThrows(InvalidGroupException.class, () -> lectureService.addAll(lectures));
    }

    @Test
    void addAll_shouldThrowEntityIsAlreadyExistsException_whenLectureIsAlreadyExists() {
        Lecture lectureWithId = getLectures().get(0);
        List<Lecture> lecturesToAdd = Arrays.asList(getLectures().get(1));
        when(teacherDao.getById(1)).thenReturn(Optional.of(lectureWithId.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lectureWithId.getGroups().get(0)));
        when(lectureDao.getAll()).thenReturn(Arrays.asList(lectureWithId));
        assertThrows(EntityIsAlreadyExistsException.class, () -> lectureService.addAll(lecturesToAdd));
    }

    @Test
    void addAll_shouldCorrectlyAddAllLectures() throws InvalidEntityFieldException, EntityNotFoundException,
            InvalidGroupException, EntityIsAlreadyExistsException, InvalidTeacherException {
        Lecture lectureWithId = getLectures().get(2);
        List<Lecture> lecturesToAddAll = Arrays.asList(getLectures().get(3));
        when(teacherDao.getById(1)).thenReturn(Optional.of(lectureWithId.getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(lectureWithId.getGroups().get(0)));
        when(lectureDao.getAll()).thenReturn(Arrays.asList(getLectures().get(0)));
        lectureService.addAll(lecturesToAddAll);
        verify(lectureDao, times(1)).addAll(lecturesToAddAll);
    }

    @Test
    void addLectureToSchedule_shouldCorrectlyaddLectureToSchedule() throws InvalidClassRoomException, InvalidGroupException,
            InvalidTeacherException {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Lecture lecture = getLectures().get(7);
        Schedule schedule = new Schedule(1, Arrays.asList(getLectures().get(0)), date, faculty);
        lectureService.addLectureToSchedule(lecture, schedule);
        verify(lectureDao, times(1)).addLectureToSchedule(lecture, schedule);
    }

    @Test
    void addLectureToSchedule_shouldThrowInvalidClassRoomException_whenClassRoomNotFree() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Lecture lecture = getLectures().get(4);
        Schedule schedule = new Schedule(1, Arrays.asList(getLectures().get(0)), date, faculty);
        assertThrows(InvalidClassRoomException.class, () -> lectureService.addLectureToSchedule(lecture, schedule));
    }

    @Test
    void addLectureToSchedule_shouldThrowInvalidTeacherException_whenTeacherNotFree() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Lecture lecture = getLectures().get(5);
        Schedule schedule = new Schedule(1, Arrays.asList(getLectures().get(0)), date, faculty);
        assertThrows(InvalidTeacherException.class, () -> lectureService.addLectureToSchedule(lecture, schedule));
    }

    @Test
    void addLectureToSchedule_shouldThrowInvalidGroupException_whenGroupNotFree() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Lecture lecture = getLectures().get(6);
        Schedule schedule = new Schedule(1, Arrays.asList(getLectures().get(0)), date, faculty);
        assertThrows(InvalidGroupException.class, () -> lectureService.addLectureToSchedule(lecture, schedule));
    }

    @Test
    void removeLectureFromSchedule_shouldCorrectlyRemoveLectureFromSchedule() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Lecture lecture = getLectures().get(0);
        Schedule schedule = new Schedule(1, Arrays.asList(lecture), date, faculty);
        lectureService.removeLectureFromSchedule(lecture, schedule);
        verify(lectureDao, times(1)).removeLectureFromSchedule(lecture, schedule);
    }

    @Test
    void getByClassRoom_shouldReturnAllLecturesByGivenClassRoom() throws EntityNotFoundException {
        List<Lecture> expected = Arrays.asList(getLectures().get(0));
        ClassRoom classRoom = getLectures().get(0).getClassRoom();
        when(teacherDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getGroups().get(0)));
        when(lectureDao.getByClassRoom(classRoom)).thenReturn(expected);
        List<Lecture> actual = lectureService.getByClassRoom(classRoom);
        assertEquals(expected, actual);
    }

    @Test
    void getBySubject_shouldReturnAllLecturesByGivenSubject() throws EntityNotFoundException {
        List<Lecture> expected = Arrays.asList(getLectures().get(0));
        Subject subject = getLectures().get(0).getSubject();
        when(teacherDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getGroups().get(0)));
        when(lectureDao.getBySubject(subject)).thenReturn(expected);
        List<Lecture> actual = lectureService.getBySubject(subject);
        assertEquals(expected, actual);
    }

    @Test
    void getTeacherLectures_shouldReturnAllLecturesByGivenTeacher() throws EntityNotFoundException {
        List<Lecture> expected = Arrays.asList(getLectures().get(0));
        Teacher teacher = getLectures().get(0).getTeacher();
        when(teacherDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getGroups().get(0)));
        when(lectureDao.getTeacherLectures(teacher)).thenReturn(expected);
        List<Lecture> actual = lectureService.getTeacherLectures(teacher);
        assertEquals(expected, actual);
    }

    @Test
    void getGroupLectures_shouldReturnAllLecturesByGivenGroup() throws EntityNotFoundException {
        List<Lecture> expected = Arrays.asList(getLectures().get(0));
        Group group = getLectures().get(0).getGroups().get(0);
        when(teacherDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getTeacher()));
        when(groupDao.getById(1)).thenReturn(Optional.of(getLectures().get(0).getGroups().get(0)));
        when(lectureDao.getGroupLectures(group)).thenReturn(expected);
        List<Lecture> actual = lectureService.getGroupLectures(group);
        assertEquals(expected, actual);
    }

    private List<Lecture> getLecturesFromDao() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom room = new ClassRoom(1, 1, 100, faculty);
        Subject subject = new Subject(1, "Java", "Learn Java");
        Teacher teacher = new Teacher(1, "Bruce", "Eckel", null);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", null, faculty));
        Lecture lecture = new Lecture(1, subject, teacher, room, groups,
                LocalTime.of(9, 00), LocalTime.of(11, 00));
        return Arrays.asList(lecture);
    }

    private List<Lecture> getLectures() {
        Faculty faculty = new Faculty(1, "Programming");
        ClassRoom firstRoom = new ClassRoom(1, 1, 100, faculty);
        Subject firstSubject = new Subject(1, "Java", "Learn Java");
        Subject secondSubject = new Subject(3, "Spring", "Learn Spring");

        Teacher teacher = new Teacher(1, "Bruce", "Eckel", Arrays.asList(firstSubject, secondSubject));
        List<Subject> groupSubjects = Arrays.asList(firstSubject, secondSubject);
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1, "AA-111", groupSubjects, faculty));
        LocalTime startTime = LocalTime.of(9, 00);
        LocalTime endTime = LocalTime.of(11, 00);

        Lecture firstLecture = new Lecture(1, firstSubject, teacher, firstRoom, groups, startTime, endTime);
        Lecture secondLecture = new Lecture(null, firstSubject, teacher, firstRoom, groups, startTime, endTime);
        Lecture thirdLecture = new Lecture(2, secondSubject, teacher, firstRoom, groups, startTime, endTime);
        Lecture fourthLecture = new Lecture(null, secondSubject, teacher, firstRoom, groups, startTime, endTime);
        Lecture fifthLecture = new Lecture(3, secondSubject, new Teacher(), firstRoom, new ArrayList<>(), startTime, endTime);
        Lecture sixthLecture = new Lecture(4, secondSubject, teacher, new ClassRoom(), new ArrayList<>(), startTime, endTime);
        Lecture seventhLecture = new Lecture(5, secondSubject, new Teacher(), new ClassRoom(), groups, startTime, endTime);
        Lecture eigthLecture = new Lecture(6, secondSubject, new Teacher(), new ClassRoom(), new ArrayList<>(), startTime, endTime);
        return Arrays.asList(firstLecture, secondLecture, thirdLecture, fourthLecture, fifthLecture, sixthLecture, seventhLecture, eigthLecture);
    }
}