package com.kharchenko.university.service.impl;

import com.kharchenko.university.dao.GroupDao;
import com.kharchenko.university.dao.LectureDao;
import com.kharchenko.university.dao.StudentDao;
import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EnitityAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.model.Faculty;
import com.kharchenko.university.model.Group;
import com.kharchenko.university.model.Lecture;
import com.kharchenko.university.model.Subject;
import com.kharchenko.university.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao groupDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private LectureDao lectureDao;

    @Override
    public Group add(Group group) {
        validateGroup(group);
        return groupDao.add(group);
    }

    @Override
    public List<Group> getAll() {
        return groupDao.getAll();
    }

    @Override
    public Group getById(Integer id) {
        return groupDao.getById(id).orElseThrow(() -> new EntityNotFoundException("Group doesn't exist with id " + id));
    }

    @Override
    public void update(Group group) {
        if (group.getId() == null) {
            throw new EntityNotFoundException("Group doesn't exist with id " + group.getId());
        }
        validateGroup(group);
        groupDao.update(group);
    }

    @Override
    public boolean deleteById(Integer id) {
        Group group = getById(id);
        if (hasLectures(group)) {
            throw new EntityHasReferenceException("Group with id " + id + " has lectures.");
        }
        if (hasStudents(group)) {
            throw new EntityHasReferenceException("Group with id " + id + " has students.");
        }
        if (hasSubjects(group)) {
            throw new EntityHasReferenceException("Group with id " + id + " has subjects.");
        }
        return groupDao.deleteById(id);
    }

    @Override
    public void addAll(List<Group> groups) {
        for (Group group : groups) {
            validateGroup(group);
        }
        groupDao.addAll(groups);
    }

    @Override
    public void addLectureToGroup(Lecture lecture, Group group) {
        groupDao.addLectureToGroup(lecture, group);
    }

    @Override
    public void removeLectureFromGroup(Lecture lecture, Group group) {
        groupDao.removeLectureFromGroup(lecture, group);
    }

    @Override
    public List<Group> getBySubject(Subject subject) {
        return groupDao.getBySubject(subject);
    }

    @Override
    public List<Group> getByFaculty(Faculty faculty) {
        return groupDao.getByFaculty(faculty);
    }

    private void validateGroup(Group group) {
        validateGroupFields(group);
        checkIfUnique(group);
    }

    private void validateGroupFields(Group group) {
        if (group.getName() == null || group.getName().isEmpty()) {
            throw new InvalidEntityFieldException("Group's name can't be empty or null");
        }
        if (group.getFaculty() == null) {
            throw new InvalidEntityFieldException("Group's faculty can't be null");
        }
    }

    private void checkIfUnique(Group group) {
        if (groupDao.getAll().stream().map(Group::getName)
                .anyMatch(name -> name.equals(group.getName()))) {
            throw new EnitityAlreadyExistsException("Group with name " + group.getName() + " is already exists");
        }
    }

    private boolean hasSubjects(Group group) {
        return !group.getSubjects().isEmpty();
    }

    private boolean hasStudents(Group group) {
        return !studentDao.getGroupStudents(group).isEmpty();
    }

    private boolean hasLectures(Group group) {
        return !lectureDao.getGroupLectures(group).isEmpty();
    }
}
