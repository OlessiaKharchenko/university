package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.ScheduleDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EnitityAlreadyExistsException;
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
import com.kharchenko.university.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureServiceImpl implements LectureService {

    @Autowired
    private LectureDao lectureDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    public Lecture add(Lecture lecture) {
        validateLecture(lecture);
        return lectureDao.add(lecture);
    }

    @Override
    public List<Lecture> getAll() {
        return getWithAllFields(lectureDao.getAll());
    }

    @Override
    public Lecture getById(Integer id) {
        Lecture lecture = lectureDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("The lecture doesn't exist with id " + id));
        setAbsentFields(lecture);
        return lecture;
    }

    @Override
    public void update(Lecture lecture) {
        if (lecture.getId() == null) {
            throw new EntityNotFoundException("The lecture doesn't exist with id " + lecture.getId());
        }
        validateLecture(lecture);
        lectureDao.update(lecture);
    }

    @Override
    public boolean deleteById(Integer id) {
        Lecture lecture = getById(id);
        if (isUsedInSchedule(lecture)) {
            throw new EntityHasReferenceException("The lecture with id " + id + " is used in schedule.");
        }
        if (isVisitedByGroups(lecture)) {
            throw new EntityHasReferenceException("The lecture with id " + id + " is visited by groups.");
        }
        return lectureDao.deleteById(id);
    }

    @Override
    public void addAll(List<Lecture> lectures) {
        for (Lecture lecture : lectures) {
            validateLecture(lecture);
        }
        lectureDao.addAll(lectures);
    }

    @Override
    public void addLectureToSchedule(Lecture lecture, Schedule schedule) {
        if (!isClassRoomFree(lecture, schedule)) {
            throw new InvalidClassRoomException("The classroom has already occupied at this time.");
        }
        if (!isTeacherFree(lecture, schedule)) {
            throw new InvalidTeacherException("The teacher already has lecture at this time.");
        }
        if (!isGroupFree(lecture, schedule)) {
            throw new InvalidGroupException("The group already has lecture at this time.");
        }
        lectureDao.addLectureToSchedule(lecture, schedule);
    }

    @Override
    public void removeLectureFromSchedule(Lecture lecture, Schedule schedule) {
        lectureDao.removeLectureFromSchedule(lecture, schedule);
    }

    @Override
    public List<Lecture> getByClassRoom(ClassRoom classRoom) {
        return getWithAllFields(lectureDao.getByClassRoom(classRoom));
    }

    @Override
    public List<Lecture> getBySubject(Subject subject) {
        return getWithAllFields(lectureDao.getBySubject(subject));
    }

    @Override
    public List<Lecture> getTeacherLectures(Teacher teacher) {
        return getWithAllFields(lectureDao.getTeacherLectures(teacher));
    }

    @Override
    public List<Lecture> getGroupLectures(Group group) {
        return getWithAllFields(lectureDao.getGroupLectures(group));
    }

    private void setAbsentFields(Lecture lecture) {
        Teacher teacher = teacherDao.getById(lecture.getTeacher().getId())
                .orElseThrow(() -> new EntityNotFoundException("The teacher doesn't exist with id " + lecture.getTeacher().getId()));
        lecture.getTeacher().setSubjects(teacher.getSubjects());
        if (!lecture.getGroups().isEmpty()) {
            for (Group lectureGroup : lecture.getGroups()) {
                Group group = groupDao.getById(lectureGroup.getId())
                        .orElseThrow(() -> new EntityNotFoundException("The group doesn't exist with id " + lectureGroup.getId()));
                lectureGroup.setSubjects(group.getSubjects());
            }
        }
    }

    private List<Lecture> getWithAllFields(List<Lecture> lectures) {
        for (Lecture lecture : lectures) {
            setAbsentFields(lecture);
        }
        return lectures;
    }

    private boolean isClassRoomFree(Lecture lecture, Schedule schedule) {
        return schedule.getLectures().stream()
                .filter(l -> l.getClassRoom().equals(lecture.getClassRoom()))
                .noneMatch(l -> l.getStartTime().equals(lecture.getStartTime()) &&
                        l.getEndTime().equals(lecture.getEndTime()) && !l.getId().equals(lecture.getId()));
    }

    private boolean isTeacherFree(Lecture lecture, Schedule schedule) {
        return schedule.getLectures().stream()
                .filter(l -> l.getTeacher().equals(lecture.getTeacher()))
                .noneMatch(l -> l.getStartTime().equals(lecture.getStartTime()) &&
                        l.getEndTime().equals(lecture.getEndTime()) && !l.getId().equals(lecture.getId()));
    }

    private boolean isGroupFree(Lecture lecture, Schedule schedule) {
        for (Group group : lecture.getGroups()) {
            if (schedule.getLectures().stream()
                    .filter(l -> l.getGroups().contains(group))
                    .anyMatch(l -> l.getStartTime().equals(lecture.getStartTime()) &&
                            l.getEndTime().equals(lecture.getEndTime()) && !l.getId().equals(lecture.getId()))) {
                return false;
            }
        }
        return true;
    }

    private void validateLecture(Lecture lecture) {
        validateLectureFields(lecture);
        checkIfUnique(lecture);
    }

    private boolean isUsedInSchedule(Lecture lecture) {
        return !scheduleDao.getByLecture(lecture).isEmpty();
    }

    private boolean isVisitedByGroups(Lecture lecture) {
        return !lecture.getGroups().isEmpty();
    }

    private boolean isTeacherQualified(Teacher teacher, Subject subject) {
        if (teacher.getSubjects() == null) {
            throw new InvalidTeacherException("The teacher without subjects can't teach on the lecture");
        }
        return teacher.getSubjects().contains(subject);
    }

    private boolean isGroupLearnSubject(Group group, Subject subject) {
        return group.getSubjects().contains(subject);
    }

    private void validateLectureFields(Lecture lecture) {
        if (lecture.getSubject() == null) {
            throw new InvalidEntityFieldException("Lecture's subject can't be null");
        }
        if (lecture.getClassRoom() == null) {
            throw new InvalidEntityFieldException("Lecture's classroom can't be null");
        }
        if (lecture.getTeacher() == null) {
            throw new InvalidEntityFieldException("Lecture's teacher can't be null");
        }
        if (lecture.getStartTime() == null) {
            throw new InvalidEntityFieldException("Lecture's start time can't be null");
        }
        if (lecture.getEndTime() == null) {
            throw new InvalidEntityFieldException("Lecture's end time can't be null");
        }
        if (!isTeacherQualified(lecture.getTeacher(), lecture.getSubject())) {
            throw new InvalidTeacherException("The teacher is not qualified in this subject.");
        }
        if (lecture.getGroups() != null) {
            for (Group group : lecture.getGroups()) {
                if (!isGroupLearnSubject(group, lecture.getSubject())) {
                    throw new InvalidGroupException("The group doesn't have this subject in its study program.");
                }
            }
        }
    }

    private void checkIfUnique(Lecture lecture) {
        if (getAll().stream()
                .anyMatch(l -> l.getSubject().equals(lecture.getSubject()) &&
                        l.getTeacher().equals(lecture.getTeacher()) &&
                        l.getClassRoom().equals(lecture.getClassRoom()) &&
                        l.getGroups().equals(lecture.getGroups()) &&
                        l.getStartTime().equals(lecture.getStartTime()) &&
                        l.getEndTime().equals(lecture.getEndTime()))) {
            throw new EnitityAlreadyExistsException("The lecture is already exists");
        }
    }
}