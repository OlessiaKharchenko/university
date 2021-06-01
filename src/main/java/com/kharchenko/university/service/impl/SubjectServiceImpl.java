package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.SubjectDao;
import com.kharchenko.university.dao.TeacherDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.model.Teacher;
import com.kharchenko.university.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private LectureDao lectureDao;

    @Override
    public Subject add(Subject subject) throws InvalidEntityFieldException, EntityIsAlreadyExistsException {
        validateSubject(subject);
        return subjectDao.add(subject);
    }

    @Override
    public List<Subject> getAll() {
        return subjectDao.getAll();
    }

    @Override
    public Subject getById(Integer id) throws EntityNotFoundException {
        return subjectDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject doesn't exist with id " + id));
    }

    @Override
    public void update(Subject subject) throws EntityNotFoundException, InvalidEntityFieldException, EntityIsAlreadyExistsException {
        if (subject.getId() == null) {
            throw new EntityNotFoundException("Subject doesn't exist with id " + subject.getId());
        }
        validateSubject(subject);
        subjectDao.update(subject);
    }

    @Override
    public boolean deleteById(Integer id) throws EntityNotFoundException, EntityHasReferenceException {
        Subject subject = getById(id);
        if (isLearntByGroups(subject)) {
            throw new EntityHasReferenceException("Subject with id " + id + " is learnt by groups.");
        }
        if (isTaughtByTeachers(subject)) {
            throw new EntityHasReferenceException("Subject with id " + id + " is taught by teachers.");
        }
        if (isTaughtOnLectures(subject)) {
            throw new EntityHasReferenceException("Subject with id " + id + " is taught on the lectures.");
        }
        return subjectDao.deleteById(id);
    }

    @Override
    public void addAll(List<Subject> subjects) throws InvalidEntityFieldException, EntityIsAlreadyExistsException {
        for (Subject subject : subjects) {
            validateSubject(subject);
        }
        subjectDao.addAll(subjects);
    }

    @Override
    public void addTeacherToSubject(Subject subject, Teacher teacher) {
        subjectDao.addTeacherToSubject(subject, teacher);
    }

    @Override
    public void addSubjectToGroup(Subject subject, Group group) {
        subjectDao.addSubjectToGroup(subject, group);
    }

    @Override
    public void removeSubjectFromGroup(Subject subject, Group group) {
        subjectDao.removeSubjectFromGroup(subject, group);
    }

    @Override
    public void removeSubjectFromTeacher(Subject subject, Teacher teacher) {
        subjectDao.removeSubjectFromTeacher(subject, teacher);
    }

    private void validateSubject(Subject subject) throws InvalidEntityFieldException, EntityIsAlreadyExistsException {
        validateSubjectFields(subject);
        checkIfUnique(subject);
    }

    private void validateSubjectFields(Subject subject) throws InvalidEntityFieldException {
        if (subject.getName() == null || subject.getName().isEmpty()) {
            throw new InvalidEntityFieldException("Subject's name can't be empty or null");
        }
        if (subject.getDescription() == null || subject.getDescription().isEmpty()) {
            throw new InvalidEntityFieldException("Subject's description can't be empty or null");
        }
    }

    private void checkIfUnique(Subject subject) throws EntityIsAlreadyExistsException {
        if (subjectDao.getAll().stream().map(Subject::getName)
                .anyMatch(name -> name.equals(subject.getName()))) {
            throw new EntityIsAlreadyExistsException("Subject with name " + subject.getName() + " is already exists");
        }
    }

    private boolean isLearntByGroups(Subject subject) {
        return !groupDao.getBySubject(subject).isEmpty();
    }

    private boolean isTaughtByTeachers(Subject subject) {
        return !teacherDao.getBySubject(subject).isEmpty();
    }

    private boolean isTaughtOnLectures(Subject subject) {
        return !lectureDao.getBySubject(subject).isEmpty();
    }
}
