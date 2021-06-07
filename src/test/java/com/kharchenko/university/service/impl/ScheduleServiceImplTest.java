package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EnitityAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private ScheduleDao scheduleDao;
    @Mock
    private LectureDao lectureDao;
    @Mock
    private TeacherDao teacherDao;
    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Test
    void changeTeacher_shouldSetOtherTeacherInAllLectures_whenGivenPeriod() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        when(teacherDao.getAll()).thenReturn(getTeachers());
        when(lectureDao.getById(1)).thenReturn(Optional.of(getLectures().get(0)));
        when(lectureDao.getById(2)).thenReturn(Optional.of(getLectures().get(1)));
        when(lectureDao.getById(3)).thenReturn(Optional.of(getLectures().get(2)));
        when(lectureDao.getById(4)).thenReturn(Optional.of(getLectures().get(3)));

        LocalDate fromDate = LocalDate.of(2021, 9, 1);
        LocalDate toDate = LocalDate.of(2021, 9, 2);
        Teacher teacher = getTeachers().get(0);
        scheduleService.changeTeacher(teacher, fromDate, toDate);
        verify(teacherDao, times(1)).getAll();
        verify(scheduleDao, times(1)).getAll();
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenScheduleDateIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Schedule schedule = new Schedule(null, null, null, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.add(schedule));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenScheduleFacultyIsNull() {
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(null, null, date, null);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.add(schedule));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureClassRoomNotBelongFaculty() {
        Faculty faculty = new Faculty(2, "Management");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(null, Arrays.asList(getLectures().get(0)), date, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.add(schedule));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenLectureGroupsNotBelongFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(null, Arrays.asList(getLectures().get(4)), date, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.add(schedule));
    }

    @Test
    void add_shouldThrowInvalidEntityFieldException_whenWeekend() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 4);
        Schedule schedule = new Schedule(null, null, date, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.add(schedule));
    }

    @Test
    void add_shouldThrowEntityIsAlreadyExistsException_whenScheduleIsAlreadyExists() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(null, null, date, faculty);
        assertThrows(EnitityAlreadyExistsException.class, () -> scheduleService.add(schedule));
    }

    @Test
    void add_shouldReturnNewSchedule_whenAddNewSchedule() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 6);
        Schedule schedule = new Schedule(null, null, date, faculty);
        Schedule expected = new Schedule(3, null, date, faculty);
        when(scheduleDao.add(schedule)).thenReturn(expected);
        Schedule actual = scheduleService.add(schedule);
        assertEquals(expected, actual);
    }

    @Test
    void getAll_shouldReturnAllSchedules() {
        when(lectureDao.getById(1)).thenReturn(Optional.of(getLectures().get(0)));
        when(lectureDao.getById(2)).thenReturn(Optional.of(getLectures().get(1)));
        when(lectureDao.getById(3)).thenReturn(Optional.of(getLectures().get(2)));
        when(lectureDao.getById(4)).thenReturn(Optional.of(getLectures().get(3)));
        when(scheduleDao.getAll()).thenReturn(getSchedulesFromDao());
        List<Schedule> expected = getSchedules();
        List<Schedule> actual = scheduleService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldReturnCorrectScheduleByGivenId() {
        Schedule expected = getSchedules().get(0);
        when(scheduleDao.getById(1)).thenReturn(Optional.of(expected));
        when(lectureDao.getById(1)).thenReturn(Optional.of(getLectures().get(0)));
        when(lectureDao.getById(2)).thenReturn(Optional.of(getLectures().get(1)));
        Schedule actual = scheduleService.getById(1);
        assertEquals(expected, actual);
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenScheduleNotExist() {
        when(scheduleDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> scheduleService.getById(7));
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenScheduleNotExist() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 6);
        Schedule schedule = new Schedule(null, null, date, faculty);
        assertThrows(EntityNotFoundException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenScheduleDateIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        Schedule schedule = new Schedule(1, null, null, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenScheduleFacultyIsNull() {
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(1, null, date, null);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureClassRoomNotBelongFaculty() {
        Faculty faculty = new Faculty(2, "Management");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(1, Arrays.asList(getLectures().get(0)), date, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenLectureGroupsNotBelongFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(1, Arrays.asList(getLectures().get(4)), date, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldThrowInvalidEntityFieldException_whenWeekend() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 4);
        Schedule schedule = new Schedule(1, null, date, faculty);
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldThrowEntityIsAlreadyExistsException_whenScheduleIsAlreadyExists() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        Schedule schedule = new Schedule(1, null, date, faculty);
        assertThrows(EnitityAlreadyExistsException.class, () -> scheduleService.update(schedule));
    }

    @Test
    void update_shouldCorrectlyUpdateSchedule() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 6);
        Schedule schedule = new Schedule(1, null, date, faculty);
        scheduleService.update(schedule);
        verify(scheduleDao, times(1)).getAll();
        verify(scheduleDao, times(1)).update(schedule);
    }

    @Test
    void deleteById_shouldThrowEntityNotFoundException_whenScheduleNotExist() {
        when(scheduleDao.getById(7)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> scheduleService.deleteById(7));
    }

    @Test
    void deleteById_shouldThrowEntityHasReferenceException_whenScheduleHasLectures() {
        Schedule schedule = getSchedules().get(0);
        when(scheduleDao.getById(1)).thenReturn(Optional.of(schedule));
        when(lectureDao.getById(1)).thenReturn(Optional.of(getLectures().get(0)));
        when(lectureDao.getById(2)).thenReturn(Optional.of(getLectures().get(1)));
        assertThrows(EntityHasReferenceException.class, () -> scheduleService.deleteById(1));
    }

    @Test
    void deleteById_shouldDeleteSchedule_whenScheduleHasNoReferences() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 4);
        Schedule schedule = new Schedule(1, new ArrayList<>(), date, faculty);
        when(scheduleDao.getById(1)).thenReturn(Optional.of(schedule));
        when(scheduleDao.deleteById(1)).thenReturn(true);
        boolean isDeleted = scheduleService.deleteById(1);
        assertTrue(isDeleted);
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenScheduleDateIsNull() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Schedule> schedules = Arrays.asList(new Schedule(null, null, null, faculty));
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.addAll(schedules));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenScheduleFacultyIsNull() {
        LocalDate date = LocalDate.of(2021, 9, 1);
        List<Schedule> schedules = Arrays.asList(new Schedule(null, null, date, null));
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.addAll(schedules));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureClassRoomNotBelongFaculty() {
        Faculty faculty = new Faculty(2, "Management");
        LocalDate date = LocalDate.of(2021, 9, 1);
        List<Schedule> schedules = Arrays.asList(new Schedule(null, Arrays.asList(getLectures().get(0)), date, faculty));
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.addAll(schedules));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenLectureGroupsNotBelongFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        List<Schedule> schedules = Arrays.asList(new Schedule(null, Arrays.asList(getLectures().get(4)), date, faculty));
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.addAll(schedules));
    }

    @Test
    void addAll_shouldThrowInvalidEntityFieldException_whenWeekend() {
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 4);
        List<Schedule> schedules = Arrays.asList(new Schedule(null, null, date, faculty));
        assertThrows(InvalidEntityFieldException.class, () -> scheduleService.addAll(schedules));
    }

    @Test
    void addAll_shouldThrowEntityIsAlreadyExistsException_whenScheduleIsAlreadyExists() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 1);
        List<Schedule> schedules = Arrays.asList(new Schedule(null, null, date, faculty));
        assertThrows(EnitityAlreadyExistsException.class, () -> scheduleService.addAll(schedules));
    }

    @Test
    void addAll_shouldCorrectlyAddAllSchedules() {
        when(scheduleDao.getAll()).thenReturn(getSchedules());
        Faculty faculty = new Faculty(1, "Programming");
        LocalDate date = LocalDate.of(2021, 9, 6);
        List<Schedule> schedules = Arrays.asList(new Schedule(null, null, date, faculty));
        scheduleService.addAll(schedules);
        verify(scheduleDao, times(1)).addAll(schedules);
    }

    @Test
    void getByLecture_shouldReturnAllSchedulesByGivenLecture() {
        List<Schedule> expected = Arrays.asList(getSchedules().get(0));
        Lecture lecture = getLectures().get(0);
        when(lectureDao.getById(1)).thenReturn(Optional.of(lecture));
        when(lectureDao.getById(2)).thenReturn(Optional.of(getLectures().get(1)));
        when(scheduleDao.getByLecture(lecture)).thenReturn(expected);
        List<Schedule> actual = scheduleService.getByLecture(lecture);
        assertEquals(expected, actual);
    }

    @Test
    void getByFaculty_shouldReturnAllSchedulesByGivenFaculty() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Schedule> expected = Arrays.asList(getSchedules().get(0));
        when(lectureDao.getById(1)).thenReturn(Optional.of(getLectures().get(0)));
        when(lectureDao.getById(2)).thenReturn(Optional.of(getLectures().get(1)));
        when(scheduleDao.getByFaculty(faculty)).thenReturn(expected);
        List<Schedule> actual = scheduleService.getByFaculty(faculty);
        assertEquals(expected, actual);
    }

    private List<Teacher> getTeachers() {
        Subject java = new Subject(1, "Java", "Learn Java");
        Subject spring = new Subject(3, "Spring", "Learn Spring");
        Teacher firstTeacher = new Teacher(1, "Bruce", "Eckel", Arrays.asList(java, spring));
        Teacher secondTeacher = new Teacher(2, "Mark", "Zuckerberg", Arrays.asList(java, spring));
        Teacher thirdTeacher = new Teacher(3, "Pavel", "Durov", Arrays.asList(java, spring));
        return Arrays.asList(firstTeacher, secondTeacher, thirdTeacher);
    }

    private List<Schedule> getSchedules() {
        Faculty faculty = new Faculty(1, "Programming");
        List<Lecture> firstDayLectures = Arrays.asList(getLectures().get(0), getLectures().get(1));
        List<Lecture> secondDayLectures = Arrays.asList(getLectures().get(2), getLectures().get(3));
        Schedule firstDay = new Schedule(1, firstDayLectures, LocalDate.of(2021, 9, 1), faculty);
        Schedule secondDay = new Schedule(2, secondDayLectures, LocalDate.of(2021, 9, 2), faculty);
        return Arrays.asList(firstDay, secondDay);
    }

    private List<Lecture> getLectures() {
        Faculty firstFaculty = new Faculty(1, "Programming");
        Faculty secondFaculty = new Faculty(2, "Management");
        ClassRoom firstRoom = new ClassRoom(1, 1, 100, firstFaculty);
        ClassRoom secondRoom = new ClassRoom(2, 1, 101, firstFaculty);
        ClassRoom thirdRoom = new ClassRoom(3, 2, 200, secondFaculty);
        Subject java = new Subject(1, "Java", "Learn Java");
        Subject spring = new Subject(2, "Spring", "Learn Spring");

        List<Group> firstGroups = Arrays.asList(new Group(1, "AA-111", Arrays.asList(java, spring), firstFaculty));
        List<Group> secondGroups = Arrays.asList(new Group(2, "BB-222", Arrays.asList(java, spring), firstFaculty));
        List<Group> thirdGroups = Arrays.asList(new Group(3, "CC-333", null, secondFaculty));
        LocalTime startLecture = LocalTime.of(9, 00);
        LocalTime endLecture = LocalTime.of(11, 00);

        Lecture oneLecture = new Lecture(1, java, getTeachers().get(0), firstRoom, firstGroups, startLecture, endLecture);
        Lecture twoLecture = new Lecture(2, spring, getTeachers().get(1), secondRoom, secondGroups, startLecture, endLecture);
        Lecture threeLecture = new Lecture(3, spring, getTeachers().get(1), secondRoom, firstGroups, startLecture, endLecture);
        Lecture fourLecture = new Lecture(4, java, getTeachers().get(0), firstRoom, secondGroups, startLecture, endLecture);
        Lecture fiveLecture = new Lecture(5, new Subject(), new Teacher(), thirdRoom, thirdGroups, startLecture, endLecture);
        return Arrays.asList(oneLecture, twoLecture, threeLecture, fourLecture, fiveLecture);
    }

    private List<Schedule> getSchedulesFromDao() {
        Faculty faculty = new Faculty(1, "Programming");
        Lecture one = new Lecture(1, null, null, null, null, null, null);
        Lecture two = new Lecture(2, null, null, null, null, null, null);
        Lecture three = new Lecture(3, null, null, null, null, null, null);
        Lecture four = new Lecture(4, null, null, null, null, null, null);
        List<Lecture> firstDayLectures = Arrays.asList(one, two);
        List<Lecture> secondDayLectures = Arrays.asList(three, four);
        Schedule firstDay = new Schedule(1, firstDayLectures, LocalDate.of(2021, 9, 1), faculty);
        Schedule secondDay = new Schedule(2, secondDayLectures, LocalDate.of(2021, 9, 2), faculty);
        return Arrays.asList(firstDay, secondDay);
    }
}